package com.verygoodsecurity.vgscollect.core.api.client

import android.util.Log
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.api.*
import com.verygoodsecurity.vgscollect.core.api.client.extension.*
import com.verygoodsecurity.vgscollect.core.model.network.NetworkRequest
import com.verygoodsecurity.vgscollect.core.model.network.NetworkResponse
import com.verygoodsecurity.vgscollect.core.model.network.VGSError
import com.verygoodsecurity.vgscollect.core.model.toMutableMap
import com.verygoodsecurity.vgscollect.util.extension.*
import com.verygoodsecurity.vgscollect.util.extension.DATA_KEY
import com.verygoodsecurity.vgscollect.util.extension.FORMAT_KEY
import com.verygoodsecurity.vgscollect.util.extension.VALUE_KEY
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import java.io.InterruptedIOException
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

internal class OkHttpClient(
    isLogsVisible: Boolean,
    private val tempStore: VgsApiTemporaryStorage
) : ApiClient {

    private val hostInterceptor = HostInterceptor()
    private val tokenizationInterceptor = TokenizationInterceptor()

    private val client: OkHttpClient by lazy {
        OkHttpClient().newBuilder()
            .addInterceptor(hostInterceptor)
            .addInterceptor(tokenizationInterceptor)
            .dispatcher(Dispatcher(Executors.newSingleThreadExecutor())).also {
                if (isLogsVisible) it.addInterceptor(HttpLoggingInterceptor())
            }
            .build()
    }

    override fun setHost(url: String?) {
        hostInterceptor.host = url?.toHost()
    }

    override fun enqueue(request: NetworkRequest, callback: ((NetworkResponse) -> Unit)?) {
        if (!request.url.isURLValid()) {
            callback?.invoke(NetworkResponse(error = VGSError.URL_NOT_VALID))
            return
        }

        tokenizationInterceptor.requiresTokenization = request.requiresTokenization

        val okHttpRequest = buildRequest(
            request.url,
            request.method,
            request.customHeader,
            request.customData,
            request.format
        )

        try {
            client.newBuilder()
                .callTimeout(request.requestTimeoutInterval, TimeUnit.MILLISECONDS)
                .readTimeout(request.requestTimeoutInterval, TimeUnit.MILLISECONDS)
                .writeTimeout(request.requestTimeoutInterval, TimeUnit.MILLISECONDS)
                .build()
                .newCall(okHttpRequest).enqueue(object : Callback {

                    override fun onFailure(call: Call, e: IOException) {
                        logException(e)
                        if (e is InterruptedIOException || e is TimeoutException) {
                            callback?.invoke(NetworkResponse(error = VGSError.TIME_OUT))
                        } else {
                            callback?.invoke(NetworkResponse(message = e.message))
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        callback?.invoke(
                            NetworkResponse(
                                response.isSuccessful,
                                response.body?.string(),
                                response.code,
                                response.message
                            )
                        )
                    }
                })
        } catch (e: Exception) {
            logException(e)
            callback?.invoke(NetworkResponse(message = e.message))
        }
    }

    override fun execute(request: NetworkRequest): NetworkResponse {
        if (!request.url.isURLValid()) {
            return NetworkResponse(error = VGSError.URL_NOT_VALID)
        }

        val okHttpRequest = buildRequest(
            request.url,
            request.method,
            request.customHeader,
            request.customData,
            request.format
        )

        return try {
            val response = client.newBuilder()
                .callTimeout(request.requestTimeoutInterval, TimeUnit.MILLISECONDS)
                .readTimeout(request.requestTimeoutInterval, TimeUnit.MILLISECONDS)
                .writeTimeout(request.requestTimeoutInterval, TimeUnit.MILLISECONDS)
                .build()
                .newCall(okHttpRequest).execute()

            NetworkResponse(
                response.isSuccessful,
                response.body?.string(),
                response.code,
                response.message
            )
        } catch (e: InterruptedIOException) {
            NetworkResponse(error = VGSError.TIME_OUT)
        } catch (e: TimeoutException) {
            NetworkResponse(error = VGSError.TIME_OUT)
        } catch (e: IOException) {
            NetworkResponse(message = e.message)
        }
    }

    override fun cancelAll() {
        client.dispatcher.cancelAll()
    }

    override fun getTemporaryStorage(): VgsApiTemporaryStorage = tempStore

    private fun buildRequest(
        url: String,
        method: HTTPMethod,
        headers: Map<String, String>?,
        data: Any?,
        contentType: VGSHttpBodyFormat = VGSHttpBodyFormat.JSON
    ): Request {
        val mediaType = contentType.toContentType().toMediaTypeOrNull()
        val requestBody = data?.toString().toRequestBodyOrNull(mediaType, method)
        return Request.Builder()
            .url(url)
            .method(method.name, requestBody)
            .addHeaders(headers)
            .build()
    }

    private fun Request.Builder.addHeaders(headers: Map<String, String>?): Request.Builder {
        headers?.forEach {
            this.addHeader(it.key, it.value)
        }
        tempStore.getCustomHeaders().forEach {
            this.addHeader(it.key, it.value)
        }

        return this
    }

    private class HostInterceptor : Interceptor {
        var host: String? = null
        override fun intercept(chain: Interceptor.Chain): Response {
            val r = with(chain.request()) {
                if (!host.isNullOrBlank() && host != url.host) {
                    val newUrl = chain.request().url.newBuilder()
                        .scheme(url.scheme)
                        .host(host!!)
                        .build()

                    chain.request().newBuilder()
                        .url(newUrl)
                        .build()
                } else {
                    this
                }
            }

            return chain.proceed(r)
        }
    }

    private class TokenizationInterceptor : Interceptor {

        var requiresTokenization: Boolean = false

        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()

            Log.e("test", "requiresTokenization: $requiresTokenization")
            return if (requiresTokenization) {
                val originalDataMap = unwrapRequestBody(originalRequest.body)
                val request = originalRequest.mapTokenizationRequest(originalDataMap)
                chain.proceed(request).mapTokenizationResponse(originalDataMap)
            } else {
                chain.proceed(originalRequest)
            }
        }

        private fun Request.mapTokenizationRequest(data: MutableList<Map<String, Any>>): Request {

            fun createRequestBody(
                request: Request,
                data: MutableList<Map<String, Any>>
            ): RequestBody {
                return data.filter {
                    (it[TOKENIZATION_REQUIRED_KEY] as? Boolean) ?: false
                }.run {
                    mutableMapOf(DATA_KEY to this)
                }.toJSON()
                    .toString()
                    .toRequestBody(
                        request.body?.contentType()
                    )
            }

            val body = createRequestBody(this, data)

            return newBuilder()
                .url(url)
                .headers(headers)
                .method(method, body)
                .build()
        }

        @Suppress("UNCHECKED_CAST")
        private fun unwrapRequestBody(body: RequestBody?): MutableList<Map<String, Any>> {
            return body?.bodyToString()
                ?.toMutableMap()
                ?.takeIf {
                    it[DATA_KEY] is MutableList<*>
                }?.run {
                    get(DATA_KEY) as MutableList<Map<String, Any>>
                } ?: mutableListOf()
        }

        private fun Response.mapTokenizationResponse(originalData: MutableList<Map<String, Any>>): Response {

            @Suppress("UNCHECKED_CAST")
            fun unwrapResponseBody(response: Response): Collection<Map<String, Any>> {
                return response.body?.string()?.toMutableMap()
                    ?.takeIf {
                        it[DATA_KEY] is Collection<*>
                    }?.run {
                        get(DATA_KEY) as Collection<Map<String, Any>>
                    } ?: mutableListOf()
            }

            @Suppress("UNCHECKED_CAST")
            fun getAlias(
                data: Collection<Map<String, Any>>,
                originalValue: String?,
                format: String?,
            ): String {
                var alias = ""

                data.filter {
                    it.containsKey(VALUE_KEY) &&
                            it[VALUE_KEY] == originalValue &&
                            it.containsKey(ALIASES_KEY)
                }.forEach {
                    val tokenizedValue = it[VALUE_KEY]
                    if (originalValue == tokenizedValue) {
                        (it[ALIASES_KEY] as? Collection<Map<String, String>>)?.forEach {
                            if (it.containsKey(FORMAT_KEY) &&
                                it[FORMAT_KEY] == format
                            ) {
                                alias = it[ALIAS_KEY].toString()
                            }
                        }
                    }
                }

                return alias
            }

            val originalResponseData = unwrapResponseBody(this)

            val responseBody = originalData.map {
                val requiredTokenization: Boolean = (it[TOKENIZATION_REQUIRED_KEY] as? Boolean)
                    ?: false
                val format = it[FORMAT_KEY].toString()
                val originalValue = it[VALUE_KEY].toString()
                val fieldName = it[FIELD_NAME_KEY].toString()

                val alias = if (requiredTokenization) {
                    getAlias(originalResponseData, originalValue, format)
                } else {
                    originalValue
                }

                fieldName to alias
            }.toMap()
                .toJSON()
                .toString()
                .toResponseBody(this.body?.contentType())

            return Response.Builder()
                .request(request)
                .body(responseBody)
                .code(code)
                .protocol(protocol)
                .message(message)
                .headers(headers)
                .build()
        }

    }

    private class HttpLoggingInterceptor : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            val requestId = UUID.randomUUID().toString()
            return chain.proceed(chain.request().also {
                it.logRequest(
                    requestId,
                    it.url.toString(),
                    it.method,
                    it.headers.toMap(),
                    it.body.bodyToString(),
                    it::class.java.simpleName
                )
            }).also {
                logResponse(
                    requestId,
                    it.request.url.toString(),
                    it.code,
                    it.message,
                    it.headers.toMap(),
                    it::class.java.simpleName
                )
            }
        }
    }
}
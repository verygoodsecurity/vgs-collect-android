package com.verygoodsecurity.vgscollect.core.api.client

import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.api.VGSHttpBodyFormat
import com.verygoodsecurity.vgscollect.core.api.VgsApiTemporaryStorage
import com.verygoodsecurity.vgscollect.core.api.VgsApiTemporaryStorageImpl
import com.verygoodsecurity.vgscollect.core.api.client.extension.bodyToString
import com.verygoodsecurity.vgscollect.core.api.client.extension.logException
import com.verygoodsecurity.vgscollect.core.api.client.extension.logRequest
import com.verygoodsecurity.vgscollect.core.api.client.extension.logResponse
import com.verygoodsecurity.vgscollect.core.api.client.extension.toRequestBodyOrNull
import com.verygoodsecurity.vgscollect.core.api.isURLValid
import com.verygoodsecurity.vgscollect.core.api.toContentType
import com.verygoodsecurity.vgscollect.core.api.toHost
import com.verygoodsecurity.vgscollect.core.model.network.NetworkException
import com.verygoodsecurity.vgscollect.core.model.network.NetworkRequest
import com.verygoodsecurity.vgscollect.core.model.network.NetworkResponse
import com.verygoodsecurity.vgscollect.core.model.network.VGSError
import com.verygoodsecurity.vgscollect.core.model.toMutableMap
import com.verygoodsecurity.vgscollect.util.NetworkInspector
import com.verygoodsecurity.vgscollect.util.extension.ALIASES_KEY
import com.verygoodsecurity.vgscollect.util.extension.ALIAS_KEY
import com.verygoodsecurity.vgscollect.util.extension.DATA_KEY
import com.verygoodsecurity.vgscollect.util.extension.FIELD_NAME_KEY
import com.verygoodsecurity.vgscollect.util.extension.FORMAT_KEY
import com.verygoodsecurity.vgscollect.util.extension.TOKENIZATION_REQUIRED_KEY
import com.verygoodsecurity.vgscollect.util.extension.VALUE_KEY
import com.verygoodsecurity.vgscollect.util.extension.toJSON
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import java.io.InterruptedIOException
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

internal class OkHttpClient(private val networkInspector: NetworkInspector) : ApiClient {

    private val storage: VgsApiTemporaryStorage = VgsApiTemporaryStorageImpl()
    private val hostInterceptor = HostInterceptor()
    private val tokenizationInterceptor = TokenizationInterceptor()

    private val client: OkHttpClient by lazy {
        OkHttpClient().newBuilder()
            .addInterceptor(hostInterceptor)
            .addInterceptor(tokenizationInterceptor)
            .dispatcher(Dispatcher(Executors.newSingleThreadExecutor()))
            .addInterceptor(HttpLoggingInterceptor())
            .build()
    }

    override fun setHost(url: String?) {
        hostInterceptor.host = url?.toHost()
    }

    override fun enqueue(request: NetworkRequest, callback: ((NetworkResponse) -> Unit)?) {
        try {
            checkConnection()
        } catch (e: NetworkException) {
            callback?.invoke(NetworkResponse(error = e.error))
            return
        }

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
                        if (e is InterruptedIOException) {
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
        try {
            checkConnection()
        } catch (e: NetworkException) {
            return NetworkResponse(error = e.error)
        }

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
                .connectTimeout(request.requestTimeoutInterval, TimeUnit.MILLISECONDS)
                .readTimeout(request.requestTimeoutInterval, TimeUnit.MILLISECONDS)
                .writeTimeout(request.requestTimeoutInterval, TimeUnit.MILLISECONDS)
                .callTimeout(request.requestTimeoutInterval, TimeUnit.MILLISECONDS)
                .build()
                .newCall(okHttpRequest).execute()

            NetworkResponse(
                response.isSuccessful,
                response.body?.string(),
                response.code,
                response.message
            )
        } catch (_: InterruptedIOException) {
            NetworkResponse(error = VGSError.TIME_OUT)
        } catch (e: Exception) {
            NetworkResponse(message = e.message)
        }
    }

    override fun cancelAll() {
        client.dispatcher.cancelAll()
    }

    override fun getTemporaryStorage(): VgsApiTemporaryStorage = storage

    @Throws(NetworkException::class)
    private fun checkConnection() {
        if (!networkInspector.hasInternetPermission()) {
            throw NetworkException(error = VGSError.NO_INTERNET_PERMISSIONS)
        } else if (!networkInspector.isConnectionAvailable()) {
            throw NetworkException(error = VGSError.NO_NETWORK_CONNECTIONS)
        }
    }

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
        storage.getCustomHeaders().forEach {
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

            return if (requiresTokenization) {
                val originalDataMap = unwrapRequestBody(originalRequest.body)
                val request = mapTokenizationRequest(originalRequest, originalDataMap)

                val response = chain.proceed(request)
                mapTokenizationResponse(response, originalDataMap)
            } else {
                chain.proceed(originalRequest)
            }
        }

        private fun mapTokenizationRequest(
            request: Request,
            data: MutableList<Map<String, Any>>
        ): Request {
            val body = data.filter {
                (it[TOKENIZATION_REQUIRED_KEY] as? Boolean) ?: false
            }.run {
                mutableMapOf(DATA_KEY to this)
            }.toJSON()
                .toString()
                .toRequestBody(
                    request.body?.contentType()
                )

            return with(request) {
                newBuilder()
                    .url(url)
                    .headers(headers)
                    .method(method, body)
                    .build()
            }
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

        @Suppress("UNCHECKED_CAST")
        fun unwrapResponseBody(request: Response): Collection<Map<String, Any>> {
            return request.body?.string()?.toMutableMap()
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

        private fun mapTokenizationResponse(
            response: Response,
            originalData: MutableList<Map<String, Any>>
        ): Response {

            val originalResponseData = unwrapResponseBody(response)

            val responseBody = originalData.associate {
                val requiredTokenization: Boolean = (it[TOKENIZATION_REQUIRED_KEY] as? Boolean) == true
                val format = it[FORMAT_KEY].toString()
                val originalValue = it[VALUE_KEY].toString()
                val fieldName = it[FIELD_NAME_KEY].toString()

                val alias = if (requiredTokenization) {
                    getAlias(originalResponseData, originalValue, format)
                } else {
                    originalValue
                }

                fieldName to alias
            }
                .toJSON()
                .toString()
                .toResponseBody(response.body?.contentType())

            return with(response) {
                Response.Builder()
                    .request(request)
                    .body(responseBody)
                    .code(code)
                    .protocol(protocol)
                    .message(message)
                    .headers(headers)
                    .build()
            }
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
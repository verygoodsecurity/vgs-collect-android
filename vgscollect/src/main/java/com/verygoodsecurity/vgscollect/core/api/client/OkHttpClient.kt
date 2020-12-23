package com.verygoodsecurity.vgscollect.core.api.client

import com.verygoodsecurity.vgscollect.BuildConfig
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.api.*
import com.verygoodsecurity.vgscollect.core.api.analityc.CollectActionTracker
import com.verygoodsecurity.vgscollect.core.api.client.ApiClient.Companion.AGENT
import com.verygoodsecurity.vgscollect.core.api.client.ApiClient.Companion.CONNECTION_TIME_OUT
import com.verygoodsecurity.vgscollect.core.api.client.ApiClient.Companion.TEMPORARY_AGENT_TEMPLATE
import com.verygoodsecurity.vgscollect.core.api.client.extension.isCodeSuccessful
import com.verygoodsecurity.vgscollect.core.api.client.extension.setMethod
import com.verygoodsecurity.vgscollect.core.model.network.NetworkRequest
import com.verygoodsecurity.vgscollect.core.model.network.NetworkResponse
import com.verygoodsecurity.vgscollect.core.model.network.VGSError
import com.verygoodsecurity.vgscollect.util.Logger
import com.verygoodsecurity.vgscollect.util.mapToJSON
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import java.io.IOException
import java.io.InterruptedIOException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

internal class OkHttpClient(
    private val isLogsVisible: Boolean = BuildConfig.DEBUG,
    private val tempStore: VgsApiTemporaryStorage
) : ApiClient {

    private val hostInterceptor: HostInterceptor = HostInterceptor()

    private val client: OkHttpClient by lazy {
        OkHttpClient().newBuilder()
            .addInterceptor(HttpLoggingInterceptor(isLogsVisible))
            .addInterceptor(hostInterceptor)
            .callTimeout(CONNECTION_TIME_OUT, TimeUnit.MILLISECONDS)
            .readTimeout(CONNECTION_TIME_OUT, TimeUnit.MILLISECONDS)
            .writeTimeout(CONNECTION_TIME_OUT, TimeUnit.MILLISECONDS)
            .dispatcher(Dispatcher(Executors.newSingleThreadExecutor()))
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

        val okHttpRequest = buildRequest(
            request.url,
            request.method,
            request.customHeader,
            request.customData,
            request.format
        )

        try {
            client.newCall(okHttpRequest).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback?.invoke(NetworkResponse(message = e.message))
                }

                override fun onResponse(call: Call, response: Response) {
                    callback?.invoke(
                        NetworkResponse(
                            response.code.isCodeSuccessful(),
                            response.body?.string(),
                            response.code
                        )
                    )
                }
            })
        } catch (e: InterruptedIOException) {
            callback?.invoke(NetworkResponse(error = VGSError.TIME_OUT))
        } catch (e: TimeoutException) {
            callback?.invoke(NetworkResponse(error = VGSError.TIME_OUT))
        } catch (e: IOException) {
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
            val response = client.newCall(okHttpRequest).execute()

            if (response.isSuccessful) {
                NetworkResponse(response.isSuccessful, response.body?.string(), response.code)
            } else {
                NetworkResponse(message = response.message, code = response.code)
            }
        } catch (e: InterruptedIOException) {
            NetworkResponse(error = VGSError.TIME_OUT)
        } catch (e: TimeoutException) {
            NetworkResponse(error = VGSError.TIME_OUT)
        } catch (e: IOException) {
            NetworkResponse(message = e.message)
        }
    }

    override fun getTemporaryStorage(): VgsApiTemporaryStorage = tempStore

    private fun buildRequest(
        url: String,
        method: HTTPMethod,
        headers: Map<String, String>?,
        data: Any?,
        contentType: VGSHttpBodyFormat = VGSHttpBodyFormat.JSON
    ): Request {
        return Request.Builder().url(url).setMethod(
            method,
            data?.toString(),
            contentType.toContentType().toMediaTypeOrNull()
        )
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

    private class HttpLoggingInterceptor(
        private val isLogsVisible: Boolean = BuildConfig.DEBUG
    ) : Interceptor {

        companion object {
            private fun buildRequestLog(request: Request): String {
                val builder = StringBuilder("Request")
                    .append("{")
                    .append("method=")
                    .append(request.method)
                    .append("}")

                return builder.toString()
            }

            private fun buildResponseLog(response: Response): String {
                val builder = StringBuilder("Response")
                    .append("{")
                    .append("code=")
                    .append(response.code.toString())
                    .append(", ")
                    .append("message=")
                    .append(response.message)
                    .append("}")

                return builder.toString()
            }
        }

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            if (isLogsVisible) Logger.i(VGSCollect::class.java.simpleName, buildRequestLog(request))

            val response = chain.proceed(request)
            if (isLogsVisible) Logger.i(
                VGSCollect::class.java.simpleName,
                buildResponseLog(response)
            )

            return response
        }
    }

    override fun cancelAll() {
        client.dispatcher.cancelAll()
    }
}
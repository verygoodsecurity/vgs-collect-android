package com.verygoodsecurity.vgscollect.core.api

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import com.verygoodsecurity.vgscollect.BuildConfig
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.model.network.VGSError
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.parseVGSResponse
import com.verygoodsecurity.vgscollect.util.Logger
import com.verygoodsecurity.vgscollect.util.mapToJSON
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.io.InterruptedIOException
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

internal class OkHttpClient(
    private val context: Context
):ApiClient {
    private var baseURL:String = ""

    companion object {
        private const val APPLICATION_JSON = "application/json; charset=utf-8"
        private const val CONNECTION_TIME_OUT = 60000L

        private const val AGENT = "VGS-CLIENT"
        private const val TEMPORARY_STR_AGENT = "source=androidSDK&medium=vgs-collect&content=${BuildConfig.VERSION_NAME}"

        fun newInstance(context: Context, baseURL:String):ApiClient {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val c = OkHttpClient(context)
                c.baseURL = baseURL
                c
            } else {
                URLConnectionClient.newInstance(context, baseURL)
            }
        }
    }

    private val client:OkHttpClient by lazy {
        OkHttpClient().newBuilder()
            .addInterceptor(HttpLoggingInterceptor())
            .callTimeout(CONNECTION_TIME_OUT, TimeUnit.MILLISECONDS)
            .readTimeout(CONNECTION_TIME_OUT, TimeUnit.MILLISECONDS)
            .writeTimeout(CONNECTION_TIME_OUT, TimeUnit.MILLISECONDS)
            .build()
    }

    private val tempStore:VgsApiTemporaryStorage by lazy {
        VgsApiTemporaryStorageImpl()
    }

    override fun call(
        path: String,
        method: HTTPMethod,
        headers: Map<String, String>?,
        data: Map<String, Any>?
    ): VGSResponse {
        if(hasNetworkAvailable().not()) {
            return notifyErrorResponse(VGSError.NO_NETWORK_CONNECTIONS)
        }

        return when(method.ordinal) {
//            HTTPMethod.GET.ordinal -> getRequest(path, headers, data)
            HTTPMethod.POST.ordinal -> postRequest(path, headers, data)
            else -> VGSResponse.ErrorResponse()
        }
    }

    private fun postRequest(
        path: String,
        headers: Map<String, String>?,
        data: Map<String, Any>?
    ): VGSResponse {

        val url = baseURL.buildURL(path = path)
            ?: return notifyErrorResponse(VGSError.URL_NOT_VALID)
        val requestBuilder = Request.Builder().url(url)

        addHeaders(requestBuilder, headers)
        addRequestBody(requestBuilder, data)

        val request = requestBuilder.build()

        return try {
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBodyStr = response.body?.string()
                val responsePayload: Map<String, Any>? = responseBodyStr?.parseVGSResponse()
                VGSResponse.SuccessResponse(responsePayload, responseBodyStr, response.code)
            } else {
                VGSResponse.ErrorResponse(response.message, response.code)
            }
        } catch (e: InterruptedIOException) {
            notifyErrorResponse(VGSError.TIME_OUT)
        } catch (e: TimeoutException) {
            notifyErrorResponse(VGSError.TIME_OUT)
        } catch (e: IOException) {
            VGSResponse.ErrorResponse(e.message)
        }
    }

    private fun addRequestBody(requestBuilder: Request.Builder, data: Map<String, Any>?) {
        val content = data?.mapToJSON().toString()
        val body = content.toRequestBody(APPLICATION_JSON.toMediaTypeOrNull())
        requestBuilder.post(body)
    }

    private fun addHeaders(requestBuilder: Request.Builder, headers: Map<String, String>?) {
        val storedHeaders = tempStore.getCustomHeaders()
        storedHeaders[AGENT] = TEMPORARY_STR_AGENT
        headers?.let { storedHeaders.putAll(headers) }
        storedHeaders.forEach {
            requestBuilder.addHeader(it.key, it.value)
        }
    }

    override fun getTemporaryStorage(): VgsApiTemporaryStorage = tempStore

    private fun hasNetworkAvailable(): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val network = manager?.activeNetworkInfo
        return (network != null)
    }

    private fun notifyErrorResponse(error: VGSError, vararg params:String?): VGSResponse.ErrorResponse {
        val message = if(params.isEmpty()) {
            context.getString(error.messageResId)
        } else {
            String.format(
                context.getString(error.messageResId),
                *params
            )
        }
        Logger.e(VGSCollect::class.java, message)
        return VGSResponse.ErrorResponse(message, error.code)
    }


    class HttpLoggingInterceptor: Interceptor {

        companion object {
            private fun buildRequestLog(request: Request):String {
                val builder = StringBuilder("Request")
                    .append("{")
                    .append("method=")
                    .append(request.method)
                    .append("}")

                return builder.toString()
            }

            private fun buildResponseLog(response: Response):String {
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
            Logger.i(VGSCollect::class.java.simpleName, buildRequestLog(request))

            val response = chain.proceed(request)
            Logger.i(VGSCollect::class.java.simpleName, buildResponseLog(response))

            return response
        }
    }
}
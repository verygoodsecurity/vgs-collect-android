package com.verygoodsecurity.vgscollect.core.api

import android.content.Context
import android.net.ConnectivityManager
import android.util.Base64
import okhttp3.OkHttpClient
import com.verygoodsecurity.vgscollect.BuildConfig
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.model.network.VGSError
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.parseVGSResponse
import com.verygoodsecurity.vgscollect.util.Logger
import com.verygoodsecurity.vgscollect.util.mapToJSON
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.io.InterruptedIOException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

internal class AnalyticClient(
    private val context: Context
) : ApiClient {
    private var baseURL:String = ""

    private val client:OkHttpClient by lazy {
        OkHttpClient().newBuilder()
            .callTimeout(CONNECTION_TIME_OUT, TimeUnit.MILLISECONDS)
            .readTimeout(CONNECTION_TIME_OUT, TimeUnit.MILLISECONDS)
            .writeTimeout(CONNECTION_TIME_OUT, TimeUnit.MILLISECONDS)
            .build()
    }

    private fun hasNetworkAvailable(): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val network = manager?.activeNetworkInfo
        return (network != null)
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
            HTTPMethod.POST.ordinal -> postRequest(path, headers, data)
            else -> VGSResponse.ErrorResponse()
        }
    }

    override fun getTemporaryStorage(): VgsApiTemporaryStorage {
        return VgsApiTemporaryStorageImpl()
    }

    private fun postRequest(path: String, headers: Map<String, String>?, data: Map<String, Any>?): VGSResponse {
        val url = baseURL.buildURL(path = path)
            ?: return notifyErrorResponse(VGSError.URL_NOT_VALID)

        val requestBuilder = Request.Builder().url(url)
//        val requestBuilder = Request.Builder().url("http://10.0.2.2:5000/vgs")

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
        val content = data?.mapToJSON().toString().toByteArray()
        val bodyStr = Base64.encodeToString(content, Base64.NO_WRAP)
        val body = bodyStr.toRequestBody(CONTENT_TYPE.toMediaTypeOrNull())
        requestBuilder.post(body)
    }

    private fun addHeaders(requestBuilder: Request.Builder, headers: Map<String, String>?) {
        val storedHeaders = mutableMapOf<String, String>()
        headers?.let { storedHeaders.putAll(headers) }
        storedHeaders.forEach {
            requestBuilder.addHeader(it.key, it.value)
        }
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

    companion object {
        private const val CONTENT_TYPE = "application/x-www-form-urlencoded"
        private const val CONNECTION_TIME_OUT = 60000L

        fun newInstance(context: Context, baseURL:String):AnalyticClient {
            val c = AnalyticClient(context)
            c.baseURL = baseURL
            return c
        }
    }
}
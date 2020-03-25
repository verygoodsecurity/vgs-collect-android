package com.verygoodsecurity.vgscollect.core.api

import android.content.Context
import com.verygoodsecurity.vgscollect.BuildConfig
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.model.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.parseVGSResponse
import com.verygoodsecurity.vgscollect.util.mapToJSON
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

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
            val c = OkHttpClient(context)
            c.baseURL = baseURL
            return c
        }
    }

    private val client:OkHttpClient by lazy {
        OkHttpClient().newBuilder()
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
            ?: return makeErrorResponse(R.string.error_url_validation)
        val requestBuilder = Request.Builder().url(url)

        addHeaders(requestBuilder, headers)
        addRequestBody(requestBuilder, data)

        val request = requestBuilder.build()

        var responseMessage:VGSResponse
        try {
            val response = client.newCall(request).execute()

            responseMessage = if(response.isSuccessful) {
                val responseBodyStr = response.body?.string()
                val responsePayload:Map<String, Any>? = responseBodyStr?.parseVGSResponse()
                VGSResponse.SuccessResponse(responsePayload, responseBodyStr, response.code)
            } else {
                VGSResponse.ErrorResponse(response.message, response.code)
            }

        } catch (e: IOException) {
            responseMessage = VGSResponse.ErrorResponse(e.message)
        }

        return responseMessage
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

    private fun makeErrorResponse(resId: Int): VGSResponse {
        val errMessage = context.resources.getString(resId)
        return VGSResponse.ErrorResponse(errMessage)
    }
}
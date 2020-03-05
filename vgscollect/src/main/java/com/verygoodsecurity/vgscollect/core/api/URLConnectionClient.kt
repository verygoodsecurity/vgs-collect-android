package com.verygoodsecurity.vgscollect.core.api

import com.verygoodsecurity.vgscollect.BuildConfig
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.model.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.parseVGSResponse
import com.verygoodsecurity.vgscollect.util.Logger
import com.verygoodsecurity.vgscollect.util.mapToJSON
import java.net.HttpURLConnection.HTTP_OK
import java.io.*
import java.net.HttpURLConnection
import java.nio.charset.Charset

internal class URLConnectionClient:ApiClient {

    private val tempStore:VgsApiTemporaryStorage by lazy {
        VgsApiTemporaryStorageImpl()
    }

    private var baseURL:String = ""

    companion object {
        private const val CHARSET = "ISO-8859-1"

        private const val CONNECTION_TIME_OUT = 30000

        private const val CONTENT_LENGTH = "Content-Length"
        private const val CONTENT_TYPE = "Content-type"
        private const val APPLICATION_JSON = "application/json"

        private const val AGENT = "vgs-client"
        private const val TEMPORARY_STR_AGENT = "source=androidSDK&medium=vgs-collect&content=${BuildConfig.VERSION_NAME}"

        fun newInstance(baseURL:String):ApiClient {
            val client = URLConnectionClient()
            client.baseURL = baseURL
            return client
        }
    }

    override fun call(path: String, method: HTTPMethod, headers: Map<String, String>?, data: Map<String, Any>?): VGSResponse {
        return when(method.ordinal) {
            HTTPMethod.GET.ordinal -> getRequest(path, headers, data)
            HTTPMethod.POST.ordinal -> postRequest(path, headers, data)
            else -> VGSResponse.ErrorResponse()
        }
    }

    override fun getTemporaryStorage(): VgsApiTemporaryStorage = tempStore

    private fun getRequest(path: String, headers: Map<String, String>? = null, data: Map<String, Any>?): VGSResponse {
        val url = baseURL.buildURL(path = path) ?: return VGSResponse.ErrorResponse()

        var conn: HttpURLConnection? = null
        var response: VGSResponse
        try {
            conn = url.openConnection() as HttpURLConnection
            conn.useCaches = false
            conn.allowUserInteraction = false
            conn.connectTimeout = CONNECTION_TIME_OUT
            conn.readTimeout = CONNECTION_TIME_OUT
            conn.instanceFollowRedirects = false
            conn.requestMethod = HTTPMethod.GET.name

            conn.setRequestProperty(AGENT, TEMPORARY_STR_AGENT)
            headers?.forEach {
                conn.setRequestProperty( it.key, it.value )
            }
            val responseCode = conn.responseCode

            var responseStr: String? = null
            if (responseCode == HTTP_OK) {
                responseStr = conn.inputStream?.bufferedReader()?.use { it.readText() }
                response = VGSResponse.SuccessResponse(successCode = responseCode, rawResponse = responseStr)
            } else {
                response = VGSResponse.ErrorResponse()
            }

        } catch (e: Exception) {
            response = VGSResponse.ErrorResponse()
            Logger.e(VGSCollect::class.java, e.localizedMessage)
        }
        conn?.disconnect()

        return response
    }

    private fun postRequest(path: String, headers: Map<String, String>? = null, data: Map<String, Any>? = null): VGSResponse {
        val url = path.buildURL(path = path) ?: return VGSResponse.ErrorResponse()

        var conn: HttpURLConnection? = null
        var response: VGSResponse
        try {
            conn = url.openConnection() as HttpURLConnection
            conn.useCaches = false
            conn.allowUserInteraction = false
            conn.connectTimeout = CONNECTION_TIME_OUT
            conn.readTimeout = CONNECTION_TIME_OUT
            conn.instanceFollowRedirects = false
            conn.requestMethod = HTTPMethod.POST.name

            conn.setRequestProperty( CONTENT_TYPE, APPLICATION_JSON )
            conn.setRequestProperty( AGENT, TEMPORARY_STR_AGENT )
            headers?.forEach {
                conn.setRequestProperty( it.key.toUpperCase(), it.value)
            }

            val content = data?.mapToJSON().toString()
            val length = content.byteInputStream(Charset.forName(CHARSET))
            conn.setRequestProperty(CONTENT_LENGTH, length.toString())
            conn.doOutput = true

            val os = conn.outputStream
            val writer = BufferedWriter(OutputStreamWriter(os,
                CHARSET
            ))

            writer.write(content)
            writer.flush()
            writer.close()
            os.close()

            val responseCode = conn.responseCode
            response = if (responseCode == HTTP_OK) {
                val rawResponse = conn.inputStream?.bufferedReader()?.use { it.readText() }
                val responsePayload:Map<String, Any>? = rawResponse?.parseVGSResponse()
                VGSResponse.SuccessResponse(responsePayload, rawResponse, responseCode)
            } else {
                val responseStr = conn.errorStream?.bufferedReader()?.use { it.readText() }
                VGSResponse.ErrorResponse(responseStr, responseCode)
            }

        } catch (e: Exception) {
            response = VGSResponse.ErrorResponse()
            Logger.e(VGSCollect::class.java, e.localizedMessage)
        }
        conn?.disconnect()

        return response
    }
}
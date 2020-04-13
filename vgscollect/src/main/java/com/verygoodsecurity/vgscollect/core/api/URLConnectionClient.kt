package com.verygoodsecurity.vgscollect.core.api

import android.content.Context
import com.verygoodsecurity.vgscollect.BuildConfig
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.parseVGSResponse
import com.verygoodsecurity.vgscollect.util.Logger
import com.verygoodsecurity.vgscollect.util.mapToJSON
import java.io.*
import java.net.HttpURLConnection
import java.net.HttpURLConnection.HTTP_OK
import java.net.URL
import java.nio.charset.Charset
import javax.net.ssl.HttpsURLConnection


@Deprecated("from 1.1.0")
internal class URLConnectionClient(
    private val context: Context
):ApiClient {
    private var baseURL:String = ""

    private val tempStore:VgsApiTemporaryStorage by lazy {
        VgsApiTemporaryStorageImpl()
    }

    companion object {
        private const val CHARSET = "UTF-8"

        private const val CONNECTION_TIME_OUT = 30000

        private const val CONTENT_LENGTH = "Content-Length"
        private const val CONTENT_TYPE = "Content-type"
        private const val APPLICATION_JSON = "application/json"

        private const val AGENT = "vgs-client"
        private const val TEMPORARY_STR_AGENT = "source=androidSDK&medium=vgs-collect&content=${BuildConfig.VERSION_NAME}"

        fun newInstance(context:Context, baseURL:String):ApiClient {
            val client = URLConnectionClient(context)
            client.baseURL = baseURL
            return client
        }
    }

    override fun call(path: String, method: HTTPMethod, headers: Map<String, String>?, data: Map<String, Any>?): VGSResponse {
        return when(method.ordinal) {
//            HTTPMethod.GET.ordinal -> getRequest(path, headers, data)
            HTTPMethod.POST.ordinal -> postRequest(path, headers, data)
            else -> VGSResponse.ErrorResponse()
        }
    }

    override fun getTemporaryStorage(): VgsApiTemporaryStorage = tempStore

    private fun postRequest(path: String, headers: Map<String, String>? = null, data: Map<String, Any>? = null): VGSResponse {
        val url = baseURL.buildURL(path = path) ?: return VGSResponse.ErrorResponse()

        var connection: HttpURLConnection? = null
        var response: VGSResponse
        try {
            connection = openConnection(url)

            connection.requestMethod = HTTPMethod.POST.name

            addHeaders(connection, headers)

            writeOutput(connection, data)

            response = handleResponse(connection)
        } catch (e: IOException) {
            response = VGSResponse.ErrorResponse(e.localizedMessage)
            Logger.e(VGSCollect::class.java, e.localizedMessage)
        } finally {
            connection?.disconnect()
        }

        return response
    }

    private fun handleResponse(connection: HttpURLConnection): VGSResponse {
        val responseCode = connection.responseCode
        return if (responseCode == HTTP_OK) {
            val rawResponse = connection.inputStream?.bufferedReader()?.use { it.readText() }
            val responsePayload:Map<String, Any>? = rawResponse?.parseVGSResponse()
            VGSResponse.SuccessResponse(responsePayload, rawResponse, responseCode)
        } else {
            val responseStr = connection.errorStream?.bufferedReader()?.use { it.readText() }
            Logger.e(VGSCollect::class.java, responseStr.toString())
            VGSResponse.ErrorResponse(responseStr, responseCode)
        }
    }

    private fun writeOutput(connection: HttpURLConnection, data: Map<String, Any>?) {
        val content = data?.mapToJSON().toString().toByteArray(Charset.forName(CHARSET))

        val os: OutputStream = connection.outputStream
        os.write(content)
        os.close()
    }

    private fun addHeaders(connection: HttpURLConnection, headers: Map<String, String>?) {
        connection.setRequestProperty( CONTENT_TYPE, APPLICATION_JSON )
        connection.setRequestProperty( AGENT, TEMPORARY_STR_AGENT )
        headers?.forEach {
            connection.setRequestProperty( it.key.toUpperCase(), it.value)
        }
    }

    private fun openConnection(url: URL): HttpURLConnection {
        val connection = url.openConnection() as HttpURLConnection
        if (connection is HttpsURLConnection) {
            connection.sslSocketFactory = TLSSocketFactory()
        }

        connection.useCaches = false
        connection.allowUserInteraction = false
        connection.connectTimeout = CONNECTION_TIME_OUT
        connection.readTimeout = CONNECTION_TIME_OUT
        connection.instanceFollowRedirects = false

        return connection
    }
}
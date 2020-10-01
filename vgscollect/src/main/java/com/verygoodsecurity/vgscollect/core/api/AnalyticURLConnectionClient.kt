package com.verygoodsecurity.vgscollect.core.api

import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.parseVGSResponse
import com.verygoodsecurity.vgscollect.util.mapToJSON
import java.io.*
import java.net.HttpURLConnection
import java.net.HttpURLConnection.HTTP_OK
import java.net.URL
import java.nio.charset.Charset
import javax.net.ssl.HttpsURLConnection

@Deprecated("from 1.1.0")
internal class AnalyticURLConnectionClient : ApiClient {
    private var baseURL:String = ""

    private val tempStore:VgsApiTemporaryStorage by lazy {
        VgsApiTemporaryStorageImpl()
    }

    override fun call(path: String, method: HTTPMethod, headers: Map<String, String>?, data: Map<String, Any>?): VGSResponse {
        return when(method.ordinal) {
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
        connection.setRequestProperty( CONTENT_TYPE, CONTENT_TYPE_VALUE )
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

    companion object {
        private const val CHARSET = "UTF-8"

        private const val CONNECTION_TIME_OUT = 30000

        private const val CONTENT_TYPE = "Content-type"
        private const val CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded"

        fun newInstance(baseURL:String):ApiClient {
            val client = AnalyticURLConnectionClient()
            client.baseURL = baseURL
            return client
        }
    }
}
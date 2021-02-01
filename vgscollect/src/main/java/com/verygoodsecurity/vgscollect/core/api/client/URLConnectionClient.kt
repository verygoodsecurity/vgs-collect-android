package com.verygoodsecurity.vgscollect.core.api.client

import com.verygoodsecurity.vgscollect.BuildConfig
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.api.*
import com.verygoodsecurity.vgscollect.core.api.VgsApiTemporaryStorage
import com.verygoodsecurity.vgscollect.core.api.client.ApiClient.Companion.CONNECTION_TIME_OUT
import com.verygoodsecurity.vgscollect.core.api.client.ApiClient.Companion.CONTENT_TYPE
import com.verygoodsecurity.vgscollect.core.api.client.extension.*
import com.verygoodsecurity.vgscollect.core.api.isURLValid
import com.verygoodsecurity.vgscollect.core.api.toContentType
import com.verygoodsecurity.vgscollect.core.model.network.NetworkRequest
import com.verygoodsecurity.vgscollect.core.model.network.NetworkResponse
import com.verygoodsecurity.vgscollect.core.model.network.VGSError
import com.verygoodsecurity.vgscollect.VGSCollectLogger
import com.verygoodsecurity.vgscollect.util.extension.concatWithSlash
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

internal class URLConnectionClient(
    private val enableLogs: Boolean = BuildConfig.DEBUG,
    private val tempStore: VgsApiTemporaryStorage
) : ApiClient {
    private val submittedTasks = mutableListOf<Future<*>>()
    private val executor: ExecutorService by lazy {
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    }


    private var host: String? = null
    override fun setHost(url: String?) {
        host = url
    }

    override fun enqueue(request: NetworkRequest, callback: ((NetworkResponse) -> Unit)?) {
        var task: Future<*>? = null
        task = executor.submit {
            try {
                callback?.invoke(this.execute(request))
                submittedTasks.remove(task)
            } catch (e: Exception) {
                callback?.invoke(NetworkResponse(error = VGSError.TIME_OUT))
            }
        }
        submittedTasks.add(task)
    }

    override fun execute(request: NetworkRequest): NetworkResponse {
        return if (!request.url.isURLValid()) {
            NetworkResponse(error = VGSError.URL_NOT_VALID)
        } else {
            makeRequest(request)
        }
    }

    @Synchronized
    private fun makeRequest(request: NetworkRequest): NetworkResponse {
        return when (request.method) {
            HTTPMethod.GET -> get(request)
            HTTPMethod.POST -> post(request)
        }
    }

    private fun get(request: NetworkRequest): NetworkResponse {
        val conn = generateURL(request).openConnection()
        conn.requestMethod = request.method.toString()
        conn.useCaches = false

        return handleResponse(conn)
    }

    private fun generateURL(request: NetworkRequest): String {
        return with(URL(request.url)) {
            if (this@URLConnectionClient.host.isNullOrBlank() || this@URLConnectionClient.host == host) {
                (host concatWithSlash path).toHttps()
            } else {
                (this@URLConnectionClient.host!! concatWithSlash path).toHttps()
            }
        }
    }

    private fun post(request: NetworkRequest): NetworkResponse {
        var connection: HttpURLConnection? = null
        return try {
            connection = generateURL(request).openConnection()
                .callTimeout(CONNECTION_TIME_OUT)
                .readTimeout(CONNECTION_TIME_OUT)
                .setInstanceFollowRedirectEnabled(false)
                .setIsUserInteractionEnabled(false)
                .setCacheEnabled(false)
                .addHeader(CONTENT_TYPE, request.format.toContentType())
                .addHeaders(tempStore.getCustomHeaders())
                .addHeaders(request.customHeader)
                .setMethod(request.method)

            if (enableLogs) VGSCollectLogger.debug(message = buildRequestLog(connection))
            writeOutput(connection, request.customData)

            handleResponse(connection)
        } catch (e: Exception) {
            if (enableLogs) VGSCollectLogger.debug(message = e.localizedMessage ?: "")
            NetworkResponse(message = e.localizedMessage)
        } finally {
            connection?.disconnect()
        }
    }

    override fun cancelAll() {
        submittedTasks.forEach {
            it.cancel(true)
        }
        submittedTasks.clear()
    }

    override fun getTemporaryStorage(): VgsApiTemporaryStorage = tempStore

    private fun handleResponse(connection: HttpURLConnection): NetworkResponse {
        val responseCode = connection.responseCode

        if (enableLogs) VGSCollectLogger.debug(message = buildResponseLog(connection))

        return if (responseCode.isCodeSuccessful()) {
            val rawResponse = connection.inputStream?.bufferedReader()?.use { it.readText() }
            NetworkResponse(true, rawResponse, responseCode)
        } else {
            val responseStr = connection.errorStream?.bufferedReader()?.use { it.readText() }
            if (enableLogs) VGSCollectLogger.debug(message = responseStr.toString())
            NetworkResponse(message = responseStr, code = responseCode)
        }
    }

    private fun writeOutput(connection: HttpURLConnection, data: Any?) {
        data?.toString()?.toByteArray(Charsets.UTF_8).let {
            connection.outputStream.use { os ->
                os.write(it)
            }
        }
    }

    companion object {
        fun newInstance(
            isLogsVisible: Boolean = BuildConfig.DEBUG,
            storage: VgsApiTemporaryStorage
        ): ApiClient {
            return URLConnectionClient(isLogsVisible, storage)
        }

        private fun buildRequestLog(connection: HttpURLConnection): String {
            val builder = StringBuilder("Request")
                .append("{")
                .append("method=")
                .append(connection.requestMethod)
                .append("}")

            return builder.toString()
        }

        private fun buildResponseLog(connection: HttpURLConnection): String {
            val builder = StringBuilder("Response")
                .append("{")
                .append("code=")
                .append(connection.responseCode.toString())
                .append(", ")
                .append("message=")
                .append(connection.responseMessage)
                .append("}")

            return builder.toString()
        }
    }
}
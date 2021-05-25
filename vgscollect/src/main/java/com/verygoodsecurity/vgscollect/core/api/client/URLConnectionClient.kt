package com.verygoodsecurity.vgscollect.core.api.client

import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.api.*
import com.verygoodsecurity.vgscollect.core.api.client.ApiClient.Companion.CONTENT_TYPE
import com.verygoodsecurity.vgscollect.core.api.client.extension.*
import com.verygoodsecurity.vgscollect.core.model.network.NetworkRequest
import com.verygoodsecurity.vgscollect.core.model.network.NetworkResponse
import com.verygoodsecurity.vgscollect.core.model.network.VGSError
import com.verygoodsecurity.vgscollect.util.extension.concatWithSlash
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

internal class URLConnectionClient(
    private val isLogsVisible: Boolean,
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
            request(request)
        }
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

    @Synchronized
    private fun request(request: NetworkRequest): NetworkResponse {
        val requestId: String = UUID.randomUUID().toString()
        var connection: HttpURLConnection? = null
        return try {
            val headers = (tempStore.getCustomHeaders() + request.customHeader).toMutableMap().apply {
                this[CONTENT_TYPE] = request.format.toContentType()
            }
            connection = generateURL(request).openConnection()
                .callTimeout(request.requestTimeoutInterval)
                .readTimeout(request.requestTimeoutInterval)
                .setInstanceFollowRedirectEnabled(false)
                .setIsUserInteractionEnabled(false)
                .setCacheEnabled(false)
                .addHeaders(headers)
                .setMethod(request.method)

            logRequestIfNeeded(requestId, request, headers, connection)
            if (request.method != HTTPMethod.GET) writeOutput(connection, request.customData)
            logResponseIfNeeded(requestId, connection)
            handleResponse(connection)
        } catch (e: Exception) {
            logExceptionIfNeeded(e)
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
        return if (responseCode.isCodeSuccessful()) {
            val rawResponse = connection.inputStream?.bufferedReader()?.use { it.readText() }
            NetworkResponse(true, rawResponse, responseCode)
        } else {
            val responseStr = connection.errorStream?.bufferedReader()?.use { it.readText() }
            NetworkResponse(
                body = responseStr,
                code = responseCode,
                message = connection.responseMessage
            )
        }
    }

    private fun writeOutput(connection: HttpURLConnection, data: Any?) {
        data?.toString()?.toByteArray(Charsets.UTF_8).let {
            connection.outputStream.use { os ->
                os.write(it)
            }
        }
    }

    private fun logRequestIfNeeded(
        requestId: String,
        request: NetworkRequest,
        requestHeaders: Map<String, String>,
        connection: HttpURLConnection
    ) {
        if (!isLogsVisible) {
            return
        }
        logRequest(
            requestId,
            connection.url.toString(),
            connection.requestMethod,
            requestHeaders,
            request.customData.toString()
        )
    }

    private fun logResponseIfNeeded(requestId: String, connection: HttpURLConnection) {
        if (!isLogsVisible) {
            return
        }
        logResponse(
            requestId,
            connection.url.toString(),
            connection.responseCode,
            connection.responseMessage,
            connection.getHeaders()
        )
    }

    private fun logExceptionIfNeeded(e: Exception) {
        if (!isLogsVisible) {
            return
        }
        logException(e)
    }
}
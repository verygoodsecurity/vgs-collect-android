package com.verygoodsecurity.vgscollect.core.api.client.extension

import com.verygoodsecurity.vgscollect.VGSCollectLogger
import com.verygoodsecurity.vgscollect.VGSCollectLogger.warn

internal fun Any.logException(e: Exception, tag: String? = null) {
    warn(tag ?: this::class.java.simpleName, "e: ${e::class.java}, message: ${e.message}")
}

internal fun Any.logRequest(
    requestId: String,
    requestUrl: String,
    method: String,
    headers: Map<String, String>,
    payload: String?,
    tag: String? = null
) {
    VGSCollectLogger.debug(
        tag ?: this::class.java.simpleName,
        """
            --> Send VGSCollectSDK request id: $requestId
            --> Send VGSCollectSDK request url: $requestUrl
            --> Send VGSCollectSDK method: $method
            --> Send VGSCollectSDK request headers: $headers
            --> Send VGSCollectSDK request payload: $payload
        """
    )
}

internal fun Any.logResponse(
    requestId: String,
    requestUrl: String,
    responseCode: Int,
    responseMessage: String,
    headers: Map<String, String>,
    tag: String? = null
) {
    VGSCollectLogger.debug(
        tag ?: this::class.java.simpleName,
        """
            <-- VGSCollectSDK request id: $requestId
            <-- VGSCollectSDK request url: $requestUrl
            <-- VGSCollectSDK response code: $responseCode
            <-- VGSCollectSDK response message: $responseMessage
            <-- VGSCollectSDK response headers: $headers
        """
    )
}
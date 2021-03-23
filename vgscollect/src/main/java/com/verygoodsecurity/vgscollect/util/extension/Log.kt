package com.verygoodsecurity.vgscollect.util.extension

import com.verygoodsecurity.vgscollect.VGSCollectLogger.debug
import com.verygoodsecurity.vgscollect.VGSCollectLogger.warn
import com.verygoodsecurity.vgscollect.view.internal.BaseInputField

internal fun Any.logDebug(message: String, tag: String? = null) {
    debug(tag ?: this::class.java.simpleName, message)
}

internal fun Any.logWaring(message: String, tag: String? = null) {
    warn(tag ?: this::class.java.simpleName, message)
}

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
    logDebug(
        """
            --> Send VGSCollectSDK request id: $requestId
            --> Send VGSCollectSDK request url: $requestUrl
            --> Send VGSCollectSDK method: $method
            --> Send VGSCollectSDK request headers: $headers
            --> Send VGSCollectSDK request payload: $payload
        """,
        tag
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
    logDebug(
        """
            <-- VGSCollectSDK request id: $requestId
            <-- VGSCollectSDK request url: $requestUrl
            <-- VGSCollectSDK response code: $responseCode
            <-- VGSCollectSDK response message: $responseMessage
            <-- VGSCollectSDK response headers: $headers
        """,
        tag
    )
}

internal fun Any.logStartViewsUpdate(views: Set<BaseInputField>) {
    val contentPaths = views.map {
        with(it.getState()?.fieldName) {
            if (isNullOrEmpty()) "CONTENT PATH NOT SET OR EMPTY" else this
        }
    }
    logDebug("Start decoding revealed data for contentPaths: $contentPaths")
    if (views.any { it.getState()?.fieldName.isNullOrEmpty() }) {
        logWaring("Some subscribed views seems to have empty content path. Verify `contentPath` property is set for each view.")
    }
}
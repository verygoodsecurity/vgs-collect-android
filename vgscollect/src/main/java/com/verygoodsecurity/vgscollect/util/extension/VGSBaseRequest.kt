package com.verygoodsecurity.vgscollect.util.extension

import com.verygoodsecurity.vgscollect.core.model.network.NetworkRequest
import com.verygoodsecurity.vgscollect.core.model.network.VGSBaseRequest
import com.verygoodsecurity.vgscollect.core.model.network.VGSRequest

internal const val DEFAULT_CONNECTION_TIME_OUT = 60_000L

internal fun VGSBaseRequest.toAnalyticRequest(url: String): NetworkRequest {
    return NetworkRequest(
        method,
        url concatWithSlash path,
        customHeader,
        customData.toJSON().toString().toBase64(),
        fieldsIgnore,
        fileIgnore,
        format,
        requestTimeoutInterval
    )
}


internal fun VGSBaseRequest.toNetworkRequest(
    url: String,
    requestData: Map<String, Any>? = null
): NetworkRequest {
    return NetworkRequest(
        method,
        url concatWithSlash path,
        customHeader,
        requestData?.toJSON()?.toString() ?: customData,
        fieldsIgnore,
        fileIgnore,
        format,
        requestTimeoutInterval
    )
}
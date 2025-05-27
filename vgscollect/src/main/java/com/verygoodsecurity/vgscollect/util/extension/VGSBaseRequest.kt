package com.verygoodsecurity.vgscollect.util.extension

import com.verygoodsecurity.vgscollect.core.model.network.NetworkRequest
import com.verygoodsecurity.vgscollect.core.model.network.VGSBaseRequest

internal fun VGSBaseRequest.toNetworkRequest(
    host: String,
    requestData: Map<String, Any>? = null,
): NetworkRequest {
    val position = host.indexOf(".")

    val url = if (position < 0 || routeId.isNullOrEmpty()) {
        StringBuilder(host).append(path).toString()
    } else {
        StringBuilder(host).insert(position, routeId).insert(position, "-").append(path).toString()
    }

    return NetworkRequest(
        method,
        url,
        customHeader,
        requestData?.toJSON()?.toString() ?: customData,
        fieldsIgnore,
        fileIgnore,
        format,
        requestTimeoutInterval,
        isTokenization
    )
}
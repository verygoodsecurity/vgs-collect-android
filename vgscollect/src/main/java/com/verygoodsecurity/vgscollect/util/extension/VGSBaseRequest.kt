package com.verygoodsecurity.vgscollect.util.extension

import com.verygoodsecurity.sdk.analytics.model.VGSAnalyticsUpstream
import com.verygoodsecurity.vgscollect.core.model.VGSCollectFieldNameMappingPolicy
import com.verygoodsecurity.vgscollect.core.model.network.NetworkRequest
import com.verygoodsecurity.vgscollect.core.model.network.VGSBaseRequest
import com.verygoodsecurity.vgscollect.core.model.state.ArrayMergePolicy

internal const val DEFAULT_CONNECTION_TIME_OUT = 60_000L

internal fun VGSBaseRequest.toNetworkRequest(
    host: String,
    requestData: Map<String, Any>? = null,
): NetworkRequest {
    val position = host.indexOf(".")

    val url = if (position < 0 || routeId.isNullOrEmpty()) {
        StringBuilder(host)
            .append(path)
            .toString()
    } else {
        StringBuilder(host)
            .insert(position, routeId)
            .insert(position, "-")
            .append(path)
            .toString()
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
        upstream == VGSAnalyticsUpstream.TOKENIZATION
    )
}
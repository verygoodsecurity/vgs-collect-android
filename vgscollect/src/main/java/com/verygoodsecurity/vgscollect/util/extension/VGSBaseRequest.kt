package com.verygoodsecurity.vgscollect.util.extension

import com.verygoodsecurity.vgscollect.core.model.VGSCollectFieldNameMappingPolicy
import com.verygoodsecurity.vgscollect.core.model.network.NetworkRequest
import com.verygoodsecurity.vgscollect.core.model.network.VGSBaseRequest
import com.verygoodsecurity.vgscollect.core.model.state.ArrayMergePolicy

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
        requestTimeoutInterval,
        false
    )
}

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
        requiresTokenization
    )
}

internal fun VGSBaseRequest.prepareUserDataForCollecting(
    staticData: MutableMap<String, Any>,
    userData: MutableMap<String, Any>
): Map<String, Any> {
    val mergePolicy = when (fieldNameMappingPolicy) {
        VGSCollectFieldNameMappingPolicy.NESTED_JSON -> ArrayMergePolicy.OVERWRITE
        VGSCollectFieldNameMappingPolicy.FLAT_JSON -> ArrayMergePolicy.OVERWRITE
        VGSCollectFieldNameMappingPolicy.NESTED_JSON_WITH_ARRAYS_MERGE -> ArrayMergePolicy.MERGE
        VGSCollectFieldNameMappingPolicy.NESTED_JSON_WITH_ARRAYS_OVERWRITE -> ArrayMergePolicy.OVERWRITE
    }

    return with(staticData) {
        deepMerge(customData, mergePolicy)
        deepMerge(userData, mergePolicy)
    }
}
package com.verygoodsecurity.vgscollect.util.extension

import com.verygoodsecurity.sdk.analytics.model.MappingPolicy
import com.verygoodsecurity.sdk.analytics.model.Status
import com.verygoodsecurity.vgscollect.app.BaseTransmitActivity
import com.verygoodsecurity.vgscollect.core.model.VGSCollectFieldNameMappingPolicy

internal fun VGSCollectFieldNameMappingPolicy.toAnalyticsMappingPolicy(): MappingPolicy {
    return when (this) {
        VGSCollectFieldNameMappingPolicy.FLAT_JSON -> MappingPolicy.FLAT_JSON
        VGSCollectFieldNameMappingPolicy.NESTED_JSON -> MappingPolicy.NESTED_JSON
        VGSCollectFieldNameMappingPolicy.NESTED_JSON_WITH_ARRAYS_OVERWRITE -> MappingPolicy.NESTED_JSON_ARRAYS_OVERWRITE
        VGSCollectFieldNameMappingPolicy.NESTED_JSON_WITH_ARRAYS_MERGE -> MappingPolicy.NESTED_JSON_ARRAYS_MERGE
    }
}

internal fun BaseTransmitActivity.Status.toAnalyticsStatus(): Status {
    return when (this) {
        BaseTransmitActivity.Status.SUCCESS -> Status.OK
        BaseTransmitActivity.Status.FAILED -> Status.FAILED
        BaseTransmitActivity.Status.CLOSE -> Status.CANCELED
    }
}

internal fun Boolean.toAnalyticsStatus(): Status {
    return if (this) Status.OK else Status.FAILED
}
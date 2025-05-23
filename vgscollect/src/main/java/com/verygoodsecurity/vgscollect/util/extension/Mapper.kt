package com.verygoodsecurity.vgscollect.util.extension

import com.verygoodsecurity.sdk.analytics.model.VGSAnalyticsMappingPolicy
import com.verygoodsecurity.sdk.analytics.model.VGSAnalyticsStatus
import com.verygoodsecurity.vgscollect.app.BaseTransmitActivity
import com.verygoodsecurity.vgscollect.core.model.VGSCollectFieldNameMappingPolicy
import com.verygoodsecurity.vgscollect.core.model.state.ArrayMergePolicy

internal fun VGSCollectFieldNameMappingPolicy.toAnalyticsMappingPolicy(): VGSAnalyticsMappingPolicy {
    return when (this) {
        VGSCollectFieldNameMappingPolicy.FLAT_JSON -> VGSAnalyticsMappingPolicy.FLAT_JSON
        VGSCollectFieldNameMappingPolicy.NESTED_JSON -> VGSAnalyticsMappingPolicy.NESTED_JSON
        VGSCollectFieldNameMappingPolicy.NESTED_JSON_WITH_ARRAYS_OVERWRITE -> VGSAnalyticsMappingPolicy.NESTED_JSON_ARRAYS_OVERWRITE
        VGSCollectFieldNameMappingPolicy.NESTED_JSON_WITH_ARRAYS_MERGE -> VGSAnalyticsMappingPolicy.NESTED_JSON_ARRAYS_MERGE
    }
}

internal fun VGSCollectFieldNameMappingPolicy.toArrayMergePolicy(): ArrayMergePolicy {
    return when (this) {
        VGSCollectFieldNameMappingPolicy.NESTED_JSON -> ArrayMergePolicy.OVERWRITE
        VGSCollectFieldNameMappingPolicy.FLAT_JSON -> ArrayMergePolicy.OVERWRITE
        VGSCollectFieldNameMappingPolicy.NESTED_JSON_WITH_ARRAYS_MERGE -> ArrayMergePolicy.MERGE
        VGSCollectFieldNameMappingPolicy.NESTED_JSON_WITH_ARRAYS_OVERWRITE -> ArrayMergePolicy.OVERWRITE
    }
}

internal fun BaseTransmitActivity.Status.toAnalyticsStatus(): VGSAnalyticsStatus {
    return when (this) {
        BaseTransmitActivity.Status.SUCCESS -> VGSAnalyticsStatus.OK
        BaseTransmitActivity.Status.FAILED -> VGSAnalyticsStatus.FAILED
        BaseTransmitActivity.Status.CLOSE -> VGSAnalyticsStatus.CANCELED
    }
}

internal fun Boolean.toAnalyticsStatus(): VGSAnalyticsStatus {
    return if (this) VGSAnalyticsStatus.OK else VGSAnalyticsStatus.FAILED
}
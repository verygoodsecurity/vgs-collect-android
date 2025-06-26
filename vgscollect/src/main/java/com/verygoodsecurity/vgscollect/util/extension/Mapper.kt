package com.verygoodsecurity.vgscollect.util.extension

import com.verygoodsecurity.sdk.analytics.model.VGSAnalyticsMappingPolicy
import com.verygoodsecurity.sdk.analytics.model.VGSAnalyticsStatus
import com.verygoodsecurity.vgscollect.app.BaseTransmitActivity
import com.verygoodsecurity.vgscollect.core.model.VGSCollectFieldNameMappingPolicy

internal fun VGSCollectFieldNameMappingPolicy.toAnalyticsMappingPolicy(): VGSAnalyticsMappingPolicy {
    return when (this) {
        VGSCollectFieldNameMappingPolicy.FLAT_JSON -> VGSAnalyticsMappingPolicy.FLAT_JSON
        VGSCollectFieldNameMappingPolicy.NESTED_JSON -> VGSAnalyticsMappingPolicy.NESTED_JSON
        VGSCollectFieldNameMappingPolicy.NESTED_JSON_WITH_ARRAYS_OVERWRITE -> VGSAnalyticsMappingPolicy.NESTED_JSON_ARRAYS_OVERWRITE
        VGSCollectFieldNameMappingPolicy.NESTED_JSON_WITH_ARRAYS_MERGE -> VGSAnalyticsMappingPolicy.NESTED_JSON_ARRAYS_MERGE
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
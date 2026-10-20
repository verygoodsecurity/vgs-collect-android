package com.verygoodsecurity.vgscollect.core.model.network.cmp

import com.verygoodsecurity.sdk.analytics.model.VGSAnalyticsUpstream
import com.verygoodsecurity.vgscollect.BuildConfig
import com.verygoodsecurity.vgscollect.core.model.network.VGSBaseRequest

internal const val CMP_ATTRIBUTES_KEY = "attributes"
internal const val CMP_DATA_KEY = "data"
internal const val CMP_META_KEY = "meta"

private const val CMP_SOURCE = "_source"
private const val CMP_MEDIUM = "_medium"
private const val CMP_FORM_ID = "_formId"
private const val CMP_VERSION = "_version"

private val RESERVED_META_KEYS = setOf(CMP_SOURCE, CMP_MEDIUM, CMP_VERSION, CMP_FORM_ID)

internal abstract class VGSCardManagementPlatformRequest : VGSBaseRequest() {

    override val upstream: VGSAnalyticsUpstream = VGSAnalyticsUpstream.CMP

    internal open val customMeta: Map<String, String> = emptyMap()

    internal fun getMeta(sessionFormId: String?): Map<String, String> {
        return buildMap {
            // SDK-owned keys are put last so they stay authoritative over caller values.
            putAll(customMeta.filterKeys { it !in RESERVED_META_KEYS })
            put(CMP_SOURCE, "vgs-collect")
            put(CMP_MEDIUM, "androidSDK")
            put(CMP_VERSION, BuildConfig.VERSION_NAME)
            if (!sessionFormId.isNullOrBlank()) {
                put(CMP_FORM_ID, sessionFormId)
            }
        }
    }
}
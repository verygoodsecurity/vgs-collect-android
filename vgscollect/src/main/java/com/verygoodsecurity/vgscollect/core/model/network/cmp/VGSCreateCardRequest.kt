package com.verygoodsecurity.vgscollect.core.model.network.cmp

import com.verygoodsecurity.sdk.analytics.model.VGSAnalyticsUpstream
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.api.VGSHttpBodyFormat
import com.verygoodsecurity.vgscollect.core.model.VGSCollectFieldNameMappingPolicy
import com.verygoodsecurity.vgscollect.util.extension.DEFAULT_CONNECTION_TIME_OUT
import com.verygoodsecurity.vgscollect.util.extension.toAuthHeader

private const val CREATE_CARD_PATH = "/cards"

internal class VGSCreateCardRequest internal constructor(
    override val method: HTTPMethod,
    override val path: String,
    override val customHeader: Map<String, String>,
    override val customData: Map<String, Any>,
    override val fieldsIgnore: Boolean,
    override val fileIgnore: Boolean,
    override val format: VGSHttpBodyFormat,
    override val fieldNameMappingPolicy: VGSCollectFieldNameMappingPolicy,
    override val requestTimeoutInterval: Long,
    override val routeId: String?,
    override val customMeta: Map<String, String> = emptyMap(),
): VGSCardManagementPlatformRequest() {

    override val upstream: VGSAnalyticsUpstream = VGSAnalyticsUpstream.CMP

    class VGSRequestBuilder {

        private val customHeader: HashMap<String, String> = HashMap()

        fun setAuthToken(token: String): VGSRequestBuilder {
            this.customHeader += token.toAuthHeader()
            return this
        }

        private var meta: Map<String, String> = emptyMap()

        fun setMeta(meta: Map<String, String>): VGSRequestBuilder {
            this.meta = meta
            return this
        }

        fun build(): VGSCreateCardRequest {
            return VGSCreateCardRequest(
                method = HTTPMethod.POST,
                path = CREATE_CARD_PATH,
                customHeader = customHeader,
                customData = emptyMap(),
                fieldsIgnore = false,
                fileIgnore = false,
                format = VGSHttpBodyFormat.API_JSON,
                fieldNameMappingPolicy = VGSCollectFieldNameMappingPolicy.NESTED_JSON,
                requestTimeoutInterval = DEFAULT_CONNECTION_TIME_OUT,
                routeId = null,
                customMeta = meta
            )
        }
    }
}
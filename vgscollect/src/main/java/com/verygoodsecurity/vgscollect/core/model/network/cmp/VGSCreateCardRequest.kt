package com.verygoodsecurity.vgscollect.core.model.network.cmp

import com.verygoodsecurity.sdk.analytics.model.VGSAnalyticsUpstream
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.api.VGSHttpBodyFormat
import com.verygoodsecurity.vgscollect.core.model.VGSCollectFieldNameMappingPolicy
import com.verygoodsecurity.vgscollect.core.model.network.VGSBaseRequest
import com.verygoodsecurity.vgscollect.util.extension.DEFAULT_CONNECTION_TIME_OUT

private const val CREATE_CARD_PATH = "/cards"
internal const val CREATE_CARD_ATTRIBUTES_KEY = "attributes"
internal const val CREATE_CARD_DATA_KEY = "data"

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
) : VGSBaseRequest() {

    override val upstream: VGSAnalyticsUpstream = VGSAnalyticsUpstream.CMP

    class VGSRequestBuilder {

        fun build(): VGSCreateCardRequest {
            return VGSCreateCardRequest(
                HTTPMethod.POST,
                CREATE_CARD_PATH,
                emptyMap(),
                emptyMap(),
                false,
                false,
                VGSHttpBodyFormat.API_JSON,
                VGSCollectFieldNameMappingPolicy.NESTED_JSON,
                DEFAULT_CONNECTION_TIME_OUT,
                null
            )
        }
    }
}
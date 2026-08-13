package com.verygoodsecurity.vgscollect.core.model.network.cmp

import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.api.VGSHttpBodyFormat
import com.verygoodsecurity.vgscollect.core.model.VGSCollectFieldNameMappingPolicy
import com.verygoodsecurity.vgscollect.util.extension.DEFAULT_CONNECTION_TIME_OUT
import com.verygoodsecurity.vgscollect.util.extension.toAuthHeader

private const val UPDATE_CARD_PATH = "/cards/"

internal class VGSUpdateCardRequest internal constructor(
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
) : VGSCardManagementPlatformRequest() {

    class VGSRequestBuilder(val cardId: String) {

        private val customHeader: HashMap<String, String> = HashMap()

        fun setAuthToken(token: String): VGSRequestBuilder {
            this.customHeader += token.toAuthHeader()
            return this
        }

        fun build(): VGSUpdateCardRequest {
            return VGSUpdateCardRequest(
                method = HTTPMethod.PATCH,
                path = "$UPDATE_CARD_PATH$cardId",
                customHeader = customHeader,
                customData = emptyMap(),
                fieldsIgnore = false,
                fileIgnore = false,
                format = VGSHttpBodyFormat.API_JSON,
                fieldNameMappingPolicy = VGSCollectFieldNameMappingPolicy.NESTED_JSON,
                requestTimeoutInterval = DEFAULT_CONNECTION_TIME_OUT,
                routeId = null
            )
        }
    }
}
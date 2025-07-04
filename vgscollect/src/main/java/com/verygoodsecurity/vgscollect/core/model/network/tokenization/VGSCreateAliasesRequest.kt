package com.verygoodsecurity.vgscollect.core.model.network.tokenization

import com.verygoodsecurity.sdk.analytics.model.VGSAnalyticsUpstream
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.api.VGSHttpBodyFormat
import com.verygoodsecurity.vgscollect.core.model.VGSCollectFieldNameMappingPolicy
import com.verygoodsecurity.vgscollect.core.model.network.VGSBaseRequest
import com.verygoodsecurity.vgscollect.util.extension.ALIASES_PATH
import com.verygoodsecurity.vgscollect.util.extension.DEFAULT_CONNECTION_TIME_OUT

/**
 * Class that represents request that uses to send data to VGS server.
 */
class VGSCreateAliasesRequest internal constructor(
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

    override val upstream: VGSAnalyticsUpstream = VGSAnalyticsUpstream.TOKENIZATION

    /**
     * Creates a builder for a request that uses to send data to VGS server.
     */
    class VGSRequestBuilder {

        private var routeId: String? = null
        private var requestTimeoutInterval: Long = DEFAULT_CONNECTION_TIME_OUT

        /**
         * Defines route id for submitting data.
         *
         * @param routeId A vault route id
         */
        fun setRouteId(routeId: String): VGSRequestBuilder {
            this.routeId = routeId
            return this
        }

        /**
         * Specifies request timeout interval in milliseconds.
         *
         * @param timeout interval in milliseconds.
         */
        fun setRequestTimeoutInterval(timeout: Long): VGSRequestBuilder {
            this.requestTimeoutInterval = timeout
            return this
        }

        /**
         * Creates a VGSCreateAliasesRequest with the arguments supplied to it.
         */
        fun build(): VGSCreateAliasesRequest {
            return VGSCreateAliasesRequest(
                HTTPMethod.POST,
                ALIASES_PATH,
                emptyMap(),
                emptyMap(),
                false,
                false,
                VGSHttpBodyFormat.JSON,
                VGSCollectFieldNameMappingPolicy.NESTED_JSON,
                requestTimeoutInterval,
                routeId
            )
        }
    }
}
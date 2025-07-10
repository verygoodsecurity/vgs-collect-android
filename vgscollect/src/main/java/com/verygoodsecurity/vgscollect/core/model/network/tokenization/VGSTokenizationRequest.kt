package com.verygoodsecurity.vgscollect.core.model.network.tokenization

import com.verygoodsecurity.sdk.analytics.model.VGSAnalyticsUpstream
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.api.VGSHttpBodyFormat
import com.verygoodsecurity.vgscollect.core.model.VGSCollectFieldNameMappingPolicy
import com.verygoodsecurity.vgscollect.core.model.network.VGSBaseRequest
import com.verygoodsecurity.vgscollect.util.extension.DEFAULT_CONNECTION_TIME_OUT
import com.verygoodsecurity.vgscollect.util.extension.TOKENIZATION_PATH

/**
 * Class to collect data before tokenization.
 *
 * @param fieldsIgnore contains true if need to skip data from input fields.
 * @param fileIgnore contains true if need to skip files.
 * @param requestTimeoutInterval Specifies request timeout interval in milliseconds.
 * @param routeId Defines route id for submitting data.
 */
data class VGSTokenizationRequest internal constructor(
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
        private var method: HTTPMethod = HTTPMethod.POST
        private var path: String = TOKENIZATION_PATH
        private val customHeader: HashMap<String, String> = HashMap()
        private val customData: HashMap<String, Any> = HashMap()
        private var fieldsIgnore: Boolean = false
        private var format: VGSHttpBodyFormat = VGSHttpBodyFormat.JSON
        private var fileIgnore: Boolean = false
        private var routeId: String? = null
        private var fieldNameMappingPolicy: VGSCollectFieldNameMappingPolicy =
            VGSCollectFieldNameMappingPolicy.NESTED_JSON
        private var requestTimeoutInterval: Long = DEFAULT_CONNECTION_TIME_OUT

        /**
         * Ignore input's data in a request to the server.
         *
         * @return current builder instance
         */
        fun ignoreFields(): VGSRequestBuilder {
            fieldsIgnore = true
            return this
        }

        /**
         * Ignore files in a request to the server.
         *
         * @return current builder instance
         */
        fun ignoreFiles(): VGSRequestBuilder {
            fileIgnore = true
            return this
        }

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
         * Creates a VGSRequest with the arguments supplied to this.
         *
         * @return VGSRequest instance
         */
        fun build(): VGSTokenizationRequest {
            return VGSTokenizationRequest(
                method,
                path,
                customHeader,
                customData,
                fieldsIgnore,
                fileIgnore,
                format,
                fieldNameMappingPolicy,
                requestTimeoutInterval,
                routeId
            )
        }
    }
}
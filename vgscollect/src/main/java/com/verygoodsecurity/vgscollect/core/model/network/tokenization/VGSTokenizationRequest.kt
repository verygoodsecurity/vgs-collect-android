package com.verygoodsecurity.vgscollect.core.model.network.tokenization

import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.api.VGSHttpBodyFormat
import com.verygoodsecurity.vgscollect.core.model.VGSCollectFieldNameMappingPolicy
import com.verygoodsecurity.vgscollect.core.model.network.VGSBaseRequest
import com.verygoodsecurity.vgscollect.util.extension.DEFAULT_CONNECTION_TIME_OUT
import com.verygoodsecurity.vgscollect.util.extension.TOKENIZATION_PATH

internal data class VGSTokenizationRequest internal constructor(
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
    override val requiresTokenization: Boolean = true
) : VGSBaseRequest() {

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
         *
         * @since 1.0.10
         */
        fun ignoreFields(): VGSRequestBuilder {
            fieldsIgnore = true
            return this
        }

        /**
         * Ignore files in a request to the server.
         *
         * @return current builder instance
         * @since 1.0.10
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

        internal fun setFormat(format: VGSHttpBodyFormat): VGSRequestBuilder {
            this.format = format
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
                null
            )
        }
    }
}
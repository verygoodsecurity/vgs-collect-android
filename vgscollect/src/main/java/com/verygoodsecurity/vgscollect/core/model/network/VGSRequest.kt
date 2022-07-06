package com.verygoodsecurity.vgscollect.core.model.network

import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.api.VGSHttpBodyFormat
import com.verygoodsecurity.vgscollect.core.model.VGSCollectFieldNameMappingPolicy
import com.verygoodsecurity.vgscollect.util.extension.DEFAULT_CONNECTION_TIME_OUT

/**
 * Class to collect data before submit.
 *
 * @param method HTTP method
 * @param path path for a request
 * @param customHeader The headers to save for request.
 * @param customData The Map to save for request.
 * @param fieldsIgnore contains true if need to skip data from input fields.
 * @param fileIgnore contains true if need to skip files.
 * @param routeId Defines route id for submitting data.
 */
data class VGSRequest internal constructor(
    override val method: HTTPMethod,
    override val path: String,
    override val customHeader: Map<String, String>,
    override val customData: Map<String, Any>,
    override val fieldsIgnore: Boolean,
    override val fileIgnore: Boolean,
    override val format: VGSHttpBodyFormat,
    override val fieldNameMappingPolicy: VGSCollectFieldNameMappingPolicy,
    override val requestTimeoutInterval: Long,
    override val routeId: String? = null,
    override val requiresTokenization: Boolean = false
) : VGSBaseRequest() {

    /**
     * Creates a builder for a request that uses to send data to VGS server.
     * dialog theme.
     */
    class VGSRequestBuilder {
        private var method: HTTPMethod = HTTPMethod.POST
        private var path: String = ""
        private val customHeader: HashMap<String, String> = HashMap()
        private val customData: HashMap<String, Any> = HashMap()
        private var fieldsIgnore: Boolean = false
        private var format: VGSHttpBodyFormat = VGSHttpBodyFormat.JSON
        private var fileIgnore: Boolean = false
        private var fieldNameMappingPolicy: VGSCollectFieldNameMappingPolicy =
            VGSCollectFieldNameMappingPolicy.NESTED_JSON
        private var requestTimeoutInterval: Long = DEFAULT_CONNECTION_TIME_OUT
        private var routeId: String? = null

        /**
         * It collect custom data which will be send to the server.
         *
         * @param customData The Map to save for request.
         * @return current builder instance
         */
        fun setCustomData(customData: Map<String, Any>): VGSRequestBuilder {
            this.customData.putAll(customData)
            return this
        }

        /**
         * It collect headers which will be send to the server.
         *
         * @param customHeader The headers to save for request.
         * @return current builder instance
         */
        fun setCustomHeader(customHeader: Map<String, String>): VGSRequestBuilder {
            this.customHeader.putAll(customHeader)
            return this
        }

        /**
         * Set the path using for a request to the server.
         *
         * @param path path for a request
         * @return current builder instance
         */
        fun setPath(path: String): VGSRequestBuilder {
            this.path = path.run {
                val p = when {
                    length == 0 -> "/"
                    first() == '/' -> this
                    else -> "/$this"
                }
                p
            }
            return this
        }


        /**
         * Set the HTTP method using for a request to the server.
         *
         * @param method HTTP method
         * @return current builder instance
         */
        fun setMethod(method: HTTPMethod): VGSRequestBuilder {
            this.method = method
            return this
        }

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
         * Defines how to map fieldNames. Default is `VGSCollectFieldNameMappingPolicy.NestedJson`.
         *
         * @return current builder instance
         */
        fun setFieldNameMappingPolicy(policy: VGSCollectFieldNameMappingPolicy): VGSRequestBuilder {
            this.fieldNameMappingPolicy = policy
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
         * Specifies request timeout interval in milliseconds.
         *
         * @param timeout interval in milliseconds.
         */
        fun setRequestTimeoutInterval(timeout: Long): VGSRequestBuilder {
            this.requestTimeoutInterval = timeout
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

        internal fun setFormat(format: VGSHttpBodyFormat): VGSRequestBuilder {
            this.format = format
            return this
        }

        /**
         * Creates a VGSRequest with the arguments supplied to this.
         *
         * @return VGSRequest instance
         */
        fun build(): VGSRequest {
            return VGSRequest(
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
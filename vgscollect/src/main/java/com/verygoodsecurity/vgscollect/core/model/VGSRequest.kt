package com.verygoodsecurity.vgscollect.core.model

import com.verygoodsecurity.vgscollect.core.HTTPMethod

/**
 * Class to collect data before submit.
 *
 * @param method HTTP method
 * @param path path for a request
 * @param customHeader The headers to save for request.
 * @param customData The Map to save for request.
 *
 * @version 1.0.3
 */
data class VGSRequest private constructor(
    val method: HTTPMethod,
    val path:String,
    val customHeader:HashMap<String, String>,
    val customData:HashMap<String, String>
) {

    /**
     * Creates a builder for a request that uses to send data to VGS server.
     * dialog theme.
     */
    class VGSRequestBuilder {
        private var method: HTTPMethod = HTTPMethod.POST
        private var path:String = "/post"
        private val customHeader:HashMap<String, String> = HashMap()
        private val customData:HashMap<String, String> = HashMap()

        /**
         * It collect custom data which will be send to server.
         *
         * @param customData The Map to save for request.
         * @return current builder instance
         */
        fun setCustomData(customData:Map<String, String>):VGSRequestBuilder {
            this.customData.putAll(customData)
            return this
        }

        /**
         * It collect headers which will be send to server.
         *
         * @param customHeader The headers to save for request.
         * @return current builder instance
         */
        fun setCustomHeader(customHeader:Map<String, String>):VGSRequestBuilder {
            this.customHeader.putAll(customHeader)
            return this
        }

        /**
         * Set the path using for a request to server.
         *
         * @param path path for a request
         * @return current builder instance
         */
        fun setPath(path:String):VGSRequestBuilder {
            this.path = path
            return this
        }


        /**
         * Set the HTTP method using for a request to server.
         *
         * @param method HTTP method
         * @return current builder instance
         */
        fun setMethod(method: HTTPMethod):VGSRequestBuilder {
            this.method = method
            return this
        }

        /**
         * Creates a VGSRequest with the arguments supplied to this.
         *
         * @return VGSRequest instance
         */
        fun build():VGSRequest {
            return VGSRequest(method, path, customHeader, customData)
        }
    }
}
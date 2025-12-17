package com.verygoodsecurity.vgscollect.core.model.network

/**
 * The base class for VGS Collect responses.
 *
 * @param code The HTTP response code.
 * @param body The raw response body as a string.
 */
sealed class VGSResponse(
    val code: Int = -1,
    val body: String? = null
) {

    /**
     * A successful response from the VGS server.
     *
     * @param response The response body as a map. @suppress
     * @param rawResponse The raw response body as a string. @suppress
     * @param successCode The HTTP response code.
     */
    data class SuccessResponse(
        @Deprecated("body attribute better to use for response parsing")
        val response: Map<String, Any>? = null,
        val successCode: Int = -1,
        @Deprecated("body attribute better to use for response parsing")
        val rawResponse: String? = null
    ) : VGSResponse(successCode, rawResponse) {

        override fun toString(): String {
            return "Code: $successCode \n $rawResponse"
        }
    }

    /**
     * An error response from the VGS server.
     *
     * @param localizeMessage A localized message describing the error.
     * @param errorCode The HTTP response code.
     */
    data class ErrorResponse(
        val localizeMessage: String = "Can't connect to server",
        val errorCode: Int = -1,
        private val rawResponse: String? = null
    ) : VGSResponse(errorCode, rawResponse) {
        override fun toString(): String {
            return "Code: $errorCode\n $localizeMessage\n $body"
        }
    }
}
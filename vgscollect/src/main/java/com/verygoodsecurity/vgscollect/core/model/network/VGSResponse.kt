package com.verygoodsecurity.vgscollect.core.model.network

/**
 * The base class definition for a VGSCollect response states.
 *
 * @param code The response code from server.
 * @param body The response string.
 */
sealed class VGSResponse(
    val code: Int = -1,
    val body: String? = null
) {

    /**
     * The class definition for a success response state.
     *
     * @param response The response map<String, *> from server.
     * @param rawResponse The response string.
     * @param successCode The response code from server.
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
     * The class definition for an error response state.

     * @param localizeMessage The message of the error.
     * @param errorCode The response code from server.
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
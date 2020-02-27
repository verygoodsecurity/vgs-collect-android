package com.verygoodsecurity.vgscollect.core.model

/**
 * The base class definition for a VGSCollect response states.
 *
 * @param code The response code from server.
 *
 * @version 1.0.1
 */
sealed class VGSResponse(val code:Int = -1) {

    /**
     * The class definition for a success response state.
     *
     * @param response The response code from server.
     * @param rawResponse The response string.
     * @param successCode The response code from server.
     */
    class SuccessResponse(
        val response:Map<String, Any>? = null,
        val rawResponse:String? = null,
        val successCode:Int = -1
    ):VGSResponse(successCode)

    /**
     * The class definition for an error response state.

     * @param localizeMessage The message of the error.
     * @param errorCode The response code from server.
     */
    class ErrorResponse(
        val localizeMessage:String? = "",
        val errorCode:Int = -1
    ):VGSResponse(errorCode)

}
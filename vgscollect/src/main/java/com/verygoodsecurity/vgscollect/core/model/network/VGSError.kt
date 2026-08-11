package com.verygoodsecurity.vgscollect.core.model.network

/**
 * VGS Collect SDK error codes.
 *
 * @param code The error code.
 * @param message The error message.
 */
enum class VGSError(
    val code: Int,
    val message: String
) {
    URL_NOT_VALID(
        code = 1480,
        message = "URL is not valid"
    ),
    NO_INTERNET_PERMISSIONS(
        code = 1481,
        message = "Permission denied (missing INTERNET permission?)"
    ),
    NO_NETWORK_CONNECTIONS(
        code = 1482,
        message = "No Internet connection"
    ),
    TIME_OUT(
        code = 1483,
        message = "TimeoutException"
    ),
    AUTH_TOKEN_IS_BLANK(
        code = 1484,
        message = "Auth token is blank."
    ),
    CONFIGURATION_LOADING_FAILED(
        code = 1490,
        message = "Session initialization fails due to configuration load error."
    ),
    AUTH_HANDLER_IS_REQUIRED(
        code = 1491,
        message = "Auth handler is required when formId is provided."
    ),
    INPUT_DATA_NOT_VALID(
        code = 1001,
        message = "Field %s is not a valid"
    ),
    FIELD_NAME_NOT_SET(
        code = 1004,
        message = "Field name is not set"
    ),
    FILE_NOT_FOUND(
        code = 1101,
        message = "File not found"
    ),
    FILE_SIZE_OVER_LIMIT(
        code = 1103,
        message = "File size is over limit"
    ),
    NOT_ACTIVITY_CONTEXT(
        code = 1105,
        message = "Context is not Activity context"
    ),
}

fun VGSError.toVGSResponse(vararg params: String?): VGSResponse.ErrorResponse {
    val message = if (params.isEmpty()) message else String.format(message, *params)
    return VGSResponse.ErrorResponse(localizeMessage = message, errorCode = this.code)
}
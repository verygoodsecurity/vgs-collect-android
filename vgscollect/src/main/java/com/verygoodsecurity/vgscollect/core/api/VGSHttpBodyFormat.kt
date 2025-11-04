package com.verygoodsecurity.vgscollect.core.api

/**
 * The content type of the request body.
 */
enum class VGSHttpBodyFormat {

    /**
     * The body is a plain text.
     */
    PLAIN_TEXT,

    /**
     * The body is a JSON.
     */
    JSON,

    /**
     * The body is a JSON API.
     */
    API_JSON,

    /**
     * The body is a URL encoded form.
     */
    X_WWW_FORM_URLENCODED
}

internal fun VGSHttpBodyFormat.toContentType() = when (this) {
    VGSHttpBodyFormat.PLAIN_TEXT -> "text/plain"
    VGSHttpBodyFormat.JSON -> "application/json"
    VGSHttpBodyFormat.API_JSON -> "application/vnd.api+json"
    VGSHttpBodyFormat.X_WWW_FORM_URLENCODED -> "application/x-www-form-urlencoded"
}
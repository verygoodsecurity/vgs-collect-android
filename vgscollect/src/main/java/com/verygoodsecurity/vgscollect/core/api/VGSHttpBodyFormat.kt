package com.verygoodsecurity.vgscollect.core.api

enum class VGSHttpBodyFormat {
    PLAIN_TEXT,
    JSON,
    API_JSON,
    X_WWW_FORM_URLENCODED
}

internal fun VGSHttpBodyFormat.toContentType() = when (this) {
    VGSHttpBodyFormat.PLAIN_TEXT -> "text/plain"
    VGSHttpBodyFormat.JSON -> "application/json"
    VGSHttpBodyFormat.API_JSON -> "application/vnd.api+json"
    VGSHttpBodyFormat.X_WWW_FORM_URLENCODED -> "application/x-www-form-urlencoded"
}
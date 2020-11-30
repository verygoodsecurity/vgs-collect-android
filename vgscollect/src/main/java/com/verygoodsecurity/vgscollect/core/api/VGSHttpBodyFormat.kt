package com.verygoodsecurity.vgscollect.core.api

enum class VGSHttpBodyFormat {
    PLAIN_TEXT,
    JSON
}

internal fun VGSHttpBodyFormat.toContentType() = when (this) {
    VGSHttpBodyFormat.JSON -> "application/json"
    VGSHttpBodyFormat.PLAIN_TEXT -> "text/plain"
}
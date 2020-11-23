package com.verygoodsecurity.vgscollect.core.api

enum class VGSHttpBodyFormat {
    JSON
}

internal fun VGSHttpBodyFormat.toContentType() = when (this) {
    VGSHttpBodyFormat.JSON -> "application/json"
}
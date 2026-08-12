package com.verygoodsecurity.vgscollect.util.extension

private const val AUTH_HEADER_KEY = "Authorization"
private const val AUTH_HEADER_VALUE = "Bearer"

internal fun String.toAuthHeader(): Pair<String, String> {
    val token = if (startsWith("$AUTH_HEADER_VALUE ")) this else "$AUTH_HEADER_VALUE $this"
    return AUTH_HEADER_KEY to token
}
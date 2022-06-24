package com.verygoodsecurity.vgscollect.core.api.client.extension

import okhttp3.RequestBody
import okio.Buffer

internal fun RequestBody?.bodyToString(): String {
    if (this == null) return ""
    val buffer = Buffer()
    writeTo(buffer)
    return buffer.readUtf8()
}
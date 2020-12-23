package com.verygoodsecurity.vgscollect.util.extension

import android.util.Base64

internal infix fun String.concatWithDash(suffix: String): String {
    return when {
        suffix.isEmpty() -> this
        suffix.startsWith('-') -> this + suffix
        else -> "$this-$suffix"
    }
}

internal infix fun String.concatWithSlash(suffix: String): String = when {
    suffix.isEmpty() -> this
    suffix.startsWith("/") -> this + suffix
    else -> "$this/$suffix"
}

internal fun String.toBase64(): String {
    return Base64.encodeToString(
        this.toByteArray(Charsets.UTF_8),
        Base64.NO_WRAP
    )
}

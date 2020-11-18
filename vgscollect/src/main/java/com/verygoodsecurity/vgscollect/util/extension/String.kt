package com.verygoodsecurity.vgscollect.util.extension


internal infix fun String.concatWithDash(suffix: String): String {
    return when {
        suffix.isEmpty() -> this
        suffix[0] == '-' -> this + suffix
        else -> "$this-$suffix"
    }
}

internal infix fun String.concatWithSlash(suffix: String): String = when {
    suffix.isEmpty() -> this
    suffix.startsWith("/") -> this + suffix
    else -> "$this/$suffix"
}

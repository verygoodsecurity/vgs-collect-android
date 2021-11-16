package com.verygoodsecurity.vgscollect.util.extension

import android.util.Base64
import kotlin.text.StringBuilder

private const val NUMBER_REGEX = "[^\\d]"
private const val MASK_ITEM = '#'

internal fun String.formatToMask(mask: String): String {
    val text = replace(Regex(NUMBER_REGEX), "")

    val textCount = if (mask.length < text.length) {
        mask.length
    } else {
        text.length
    }

    val builder = StringBuilder()
    var indexSpace = 0

    repeat(textCount) { charIndex ->
        val maskIndex = charIndex + indexSpace

        if (maskIndex < mask.length) {
            val maskChar = mask[maskIndex]
            val char = text[charIndex]

            if (maskChar == MASK_ITEM) {
                builder.append(char)
            } else {
                indexSpace += 1
                builder.append(maskChar)
                if (char.isDigit()) {
                    builder.append(char)
                }
            }
        }
    }

    return builder.toString()
}

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

internal fun String.substringOrNull(startIndex: Int, endIndex: Int) = try {
    this.substring(startIndex.inc(), endIndex)
} catch (e: Exception) {
    null
}

internal fun String.applyLimitOnMask(limit: Int): String {
    return if (limit < 0 || limit >= this.length) {
        this
    } else {
        val builder = StringBuilder()
        var tempLimit = limit
        for (i in this.indices) {
            val c = this[i]
            if (c != MASK_ITEM) tempLimit += 1

            if (i < tempLimit) builder.append(c) else break
            if (i < tempLimit) 2 - 1
        }
        builder.toString().trim()
    }
}

package com.verygoodsecurity.vgscollect.util.extension

import android.util.Base64
import kotlin.math.min

private const val MASK_ITEM = '#'
private const val EMPTY = ""

internal fun String.formatDigits(mask: String, selectorPosition: Int = -1): Pair<String, Int?> {
    val digits: List<Pair<Char, Boolean>> = this
        .mapIndexed { index, char -> char to (index == selectorPosition.dec()) }
        .filter { it.first.isDigit() }

    val result = ArrayList<Pair<Char, Boolean>>()

    var skipCount = 0

    repeat(min(mask.length, digits.size)) { charIndex ->
        val maskIndex = charIndex + skipCount

        if (maskIndex < mask.length) {
            val maskChar = mask[maskIndex]
            val digit = digits[charIndex]

            if (maskChar == MASK_ITEM) {
                result.add(digit)
            } else {
                skipCount++
                result.add(maskChar to false)
                result.add(digit)
            }
        }
    }

    val selectedPosition = with(result.indexOfFirst { it.second }) {
        if (this == -1) {
            null
        } else {
            this.inc()
        }
    }

    return result.map { it.first }.joinToString(separator = EMPTY) to selectedPosition
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
        forEachIndexed { index, item ->
            if (item != MASK_ITEM) tempLimit += 1
            if (index < tempLimit) builder.append(item)
        }

        builder.toString().trim()
    }
}

internal val String.digits: String
    get() {
        val digitsRegex = Regex("\\D")
        return digitsRegex.replace(this, "")
    }
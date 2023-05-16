package com.verygoodsecurity.vgscollect.view.date

import com.verygoodsecurity.vgscollect.util.extension.digits
import com.verygoodsecurity.vgscollect.util.extension.substringOrNull
import java.text.SimpleDateFormat
import java.util.*

// TODO: Move this method to a better place
internal data class DateComponentsString(
    var day: String,
    var month: String,
    var year: String
) {
    val isDayValid = day.isNotEmpty()
    val isMonthValid = month.isNotEmpty()
    val isYearValid = year.isNotEmpty()
}

internal enum class DateFormat(val format: String) {
    MM_YYYY("MM/yyyy"),
    MM_YY("MM/yy"),
    MM_DD_YYYY("MM/dd/yyyy"),
    DD_MM_YYYY("dd/MM/yyyy"),
    YYYY_MM_DD("yyyy/MM/dd");

    internal val formatPatternItem = '#'

    internal val formatPattern: String
        get() {
            val patternItem = formatPatternItem.toString()
            return format
                .replace("M", patternItem, true)
                .replace("y", patternItem, true)
                .replace("d", patternItem, true)
        }

    fun dateComponentsString(input: String): DateComponentsString {
        val result = DateComponentsString("", "", "")

        // Get only digits
        var digits = input.digits
        when (this) {
            // Get month, day and year
            MM_DD_YYYY -> {
                // Get month
                result.month = digits.take(monthCharacters)
                digits = digits.removePrefix(result.month)
                // Get day
                result.day = digits.take(daysCharacters)
                digits = digits.removePrefix(result.day)
                // Get year
                result.year = digits.take(yearCharacters)
            }
            // Get day, month and year
            DD_MM_YYYY -> {
                // Get day
                result.day = digits.take(daysCharacters)
                digits = digits.removePrefix(result.day)
                // Get month
                result.month = digits.take(monthCharacters)
                digits = digits.removePrefix(result.month)
                // Get year
                result.year = digits.take(yearCharacters)
            }
            // Get year, month and day
            YYYY_MM_DD -> {
                // Get year
                result.year = digits.take(yearCharacters)
                digits = digits.removePrefix(result.year)
                // Get month
                result.month = digits.take(monthCharacters)
                digits = digits.removePrefix(result.month)
                // Get day
                result.day = digits.take(daysCharacters)
            }
            MM_YY, MM_YYYY -> {
                // Get month
                result.month = digits.take(monthCharacters)
                digits = digits.removePrefix(result.month)
                // Get year
                result.year = digits.take(yearCharacters)
            }
        }
        return result
    }

    internal fun dateFromString(input: String?): Date? {
        // Make sure if is a valid input string
        if (input.isNullOrEmpty()) {
            return null
        }

        val sDateFormat = SimpleDateFormat(format, Locale.US)
        sDateFormat.isLenient = false

        return try {
            sDateFormat.parse(input)
        } catch (_: Exception) {
            null
        }
    }

    companion object {

        // Default format
        const val divider = "/"

        private fun findDividerInInput(input: String?): String? {
            // Make sure if is a valid pattern string
            if (input.isNullOrEmpty()) {
                return null
            }
            // Remove all date components to get the divider
            val allDividers = input
                .replace("M", "", true)
                .replace("y", "", true)
                .replace("d", "", true)
            // If not divider, return
            if (allDividers.isEmpty()) {
                return null
            }
            // Replace dividers with the default
            return allDividers.first().toString()
        }

        internal fun parsePatternToDateFormat(pattern: String?): DateFormat? {
            // Make sure if is a valid pattern string
            if (pattern.isNullOrEmpty()) {
                return null
            }

            // Find divider
            val currentDivider = findDividerInInput(pattern) ?: return null

            // Replace dividers with the default
            return when (pattern.replace(currentDivider, divider, true)) {
                MM_YYYY.format -> MM_YYYY
                MM_YY.format -> MM_YY
                MM_DD_YYYY.format -> MM_DD_YYYY
                DD_MM_YYYY.format -> DD_MM_YYYY
                YYYY_MM_DD.format -> YYYY_MM_DD
                else -> null
            }
        }
    }
}

internal val DateFormat.daysVisible: Boolean
    get() {
        return when (this) {
            DateFormat.MM_YYYY, DateFormat.MM_YY -> false
            DateFormat.MM_DD_YYYY, DateFormat.DD_MM_YYYY, DateFormat.YYYY_MM_DD -> true
        }
    }

internal val DateFormat.daysCharacters: Int
    get() {
        return when (this) {
            DateFormat.MM_YYYY, DateFormat.MM_YY -> 0
            DateFormat.MM_DD_YYYY, DateFormat.DD_MM_YYYY, DateFormat.YYYY_MM_DD -> 2
        }
    }

internal val DateFormat.monthCharacters: Int
    get() = 2

internal val DateFormat.yearCharacters: Int
    get() {
        return when (this) {
            DateFormat.MM_YYYY, DateFormat.MM_DD_YYYY, DateFormat.DD_MM_YYYY, DateFormat.YYYY_MM_DD -> 4
            DateFormat.MM_YY -> 2
        }
    }

internal val DateFormat.dividerCharacters: Int
    get() {
        return when (this) {
            DateFormat.MM_YYYY, DateFormat.MM_YY -> 1
            DateFormat.MM_DD_YYYY, DateFormat.DD_MM_YYYY, DateFormat.YYYY_MM_DD -> 2
        }
    }

internal val DateFormat.size: Int
    get() {
        return this.daysCharacters + this.monthCharacters + this.yearCharacters + this.dividerCharacters
    }
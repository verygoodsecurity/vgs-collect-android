package com.verygoodsecurity.vgscollect.view.date

import java.text.SimpleDateFormat
import java.util.*

internal enum class DateRangeFormat(val format: String) {
    MM_DD_YYYY("MM/dd/yyyy"),
    DD_MM_YYYY("dd/MM/yyyy"),
    YYYY_MM_DD("yyyy/MM/dd");

    //region - Properties
    internal val daysCharacters = 2
    internal val monthCharacters = 2
    internal val yearCharacters = 4
    internal val dividerCharacters = 2
    internal val size = daysCharacters + monthCharacters + yearCharacters + dividerCharacters
    internal val formatPatternItem = '#'
    internal val formatPattern: String
        get() {
            val patternItem = formatPatternItem.toString()
            return format
                .replace("M", patternItem, true)
                .replace("y", patternItem, true)
                .replace("d", patternItem, true)
        }
    //endregion

    //region - Methods
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
    //endregion

    //region - Companion
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

        internal fun parsePatternToDateFormat(pattern: String?): DateRangeFormat? {
            // Make sure if is a valid pattern string
            if (pattern.isNullOrEmpty()) {
                return null
            }

            // Find divider
            val currentDivider = findDividerInInput(pattern) ?: return null

            // Replace dividers with the default
            return when (pattern.replace(currentDivider, divider, true)) {
                MM_DD_YYYY.format -> MM_DD_YYYY
                DD_MM_YYYY.format -> DD_MM_YYYY
                YYYY_MM_DD.format -> YYYY_MM_DD
                else -> null
            }
        }
    }
    //endregion
}
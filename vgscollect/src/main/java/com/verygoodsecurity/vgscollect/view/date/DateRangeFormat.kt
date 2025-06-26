package com.verygoodsecurity.vgscollect.view.date

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal sealed class DateRangeFormat(val format: String) {

    abstract val daysCharacters: Int
    abstract val monthCharacters: Int
    abstract val yearCharacters: Int
    abstract val dividerCharacters: Int
    abstract val formatPattern: String

    val size: Int by lazy { daysCharacters + monthCharacters + yearCharacters + dividerCharacters }

    val formatPatternItem = '#'

    companion object {

        // Default format
        const val DIVIDER = "/"

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
            return when (pattern.replace(currentDivider, DIVIDER, true)) {
                MMddYYYY.format -> MMddYYYY
                DDmmYYYY.format -> DDmmYYYY
                YYYYmmDD.format -> YYYYmmDD
                MMyy.format -> MMyy
                else -> null
            }
        }
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

    data object MMddYYYY : DateRangeFormat("MM/dd/yyyy") {
        override val daysCharacters: Int = 2
        override val monthCharacters: Int = 2
        override val yearCharacters: Int = 4
        override val dividerCharacters: Int = 2
        override val formatPattern: String
            get() {
                val patternItem = formatPatternItem.toString()
                return format
                    .replace("M", patternItem, true)
                    .replace("y", patternItem, true)
                    .replace("d", patternItem, true)
            }
    }

    data object DDmmYYYY : DateRangeFormat("dd/MM/yyyy") {
        override val daysCharacters: Int = 2
        override val monthCharacters: Int = 2
        override val yearCharacters: Int = 4
        override val dividerCharacters: Int = 2
        override val formatPattern: String
            get() {
                val patternItem = formatPatternItem.toString()
                return format
                    .replace("M", patternItem, true)
                    .replace("y", patternItem, true)
                    .replace("d", patternItem, true)
            }
    }

    data object YYYYmmDD : DateRangeFormat("yyyy/MM/dd") {
        override val daysCharacters: Int = 2
        override val monthCharacters: Int = 2
        override val yearCharacters: Int = 4
        override val dividerCharacters: Int = 2
        override val formatPattern: String
            get() {
                val patternItem = formatPatternItem.toString()
                return format
                    .replace("M", patternItem, true)
                    .replace("y", patternItem, true)
                    .replace("d", patternItem, true)
            }

    }

    data object MMyy : DateRangeFormat("MM/yy") {
        override val daysCharacters: Int = 0
        override val monthCharacters: Int = 2
        override val yearCharacters: Int = 2
        override val dividerCharacters: Int = 1
        override val formatPattern: String
            get() {
                val patternItem = formatPatternItem.toString()
                return format
                    .replace("M", patternItem, true)
                    .replace("y", patternItem, true)
            }

    }
}
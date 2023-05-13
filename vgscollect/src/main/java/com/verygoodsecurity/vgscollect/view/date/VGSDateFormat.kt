package com.verygoodsecurity.vgscollect.view.date

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal sealed interface IDateFormat {
    val format: String
}

sealed class VGSDateFormat : IDateFormat {

    class mmyyyy : VGSDateFormat() {
        override val format = "MM/yyyy"
    }

    class mmyy : VGSDateFormat() {
        override val format = "MM/yy"
    }

    class mmddyyyy : VGSDateFormat() {
        override val format = "MM/dd/yyyy"
    }

    class ddmmyyyy : VGSDateFormat() {
        override val format = "dd/MM/yyyy"
    }

    class yyyymmdd : VGSDateFormat() {
        override val format = "yyyy/MM/dd"
    }

    internal val daysVisible: Boolean
        get() {
            return this is mmddyyyy || this is ddmmyyyy || this is yyyymmdd
        }

    internal val daysCharacters: Int
        get() {
            return when (this) {
                is mmddyyyy, is ddmmyyyy, is yyyymmdd -> 1
                else -> 0
            }
        }
    internal val monthCharacters = 2
    internal val yearCharacters: Int
        get() {
            return when (this) {
                is mmyyyy, is mmddyyyy, is ddmmyyyy, is yyyymmdd -> 4
                else -> 2
            }
        }
    internal val dividerCharacters: Int
        get() {
            return when (this) {
                is mmyyyy, is mmyy -> 1
                else -> 2
            }
        }

    internal val size: Int = daysCharacters + monthCharacters + yearCharacters + dividerCharacters

    fun dateFromString(input: String?): Date? {
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

        internal fun parsePatternToDateFormat(pattern: String?): VGSDateFormat? {
            // Make sure if is a valid pattern string
            if (pattern.isNullOrEmpty()) {
                return null
            }

            // Find divider
            val currentDivider = findDividerInInput(pattern) ?: return null

            // Replace dividers with the default
            return when (pattern.replace(currentDivider, divider, true)) {
                mmyyyy().format -> mmyyyy()
                mmyy().format -> mmyy()
                mmddyyyy().format -> mmddyyyy()
                ddmmyyyy().format -> ddmmyyyy()
                yyyymmdd().format -> yyyymmdd()
                else -> null
            }
        }
    }
}
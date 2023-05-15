package com.verygoodsecurity.vgscollect.view.date

import java.text.SimpleDateFormat
import java.util.*

internal enum class VGSDateFormat(val format: String) {
    mmyyyy("MM/yyyy"),
    mmyy("MM/yy"),
    mmddyyyy("MM/dd/yyyy"),
    ddmmyyyy("dd/MM/yyyy"),
    yyyymmdd("yyyy/MM/dd");

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
                mmyyyy.format -> mmyyyy
                mmyy.format -> mmyy
                mmddyyyy.format -> mmddyyyy
                ddmmyyyy.format -> ddmmyyyy
                yyyymmdd.format -> yyyymmdd
                else -> null
            }
        }
    }
}

internal val VGSDateFormat.daysVisible: Boolean
    get() {
        return when (this) {
            VGSDateFormat.mmyyyy, VGSDateFormat.mmyy -> false
            VGSDateFormat.mmddyyyy, VGSDateFormat.ddmmyyyy, VGSDateFormat.yyyymmdd -> true
        }
    }

internal val VGSDateFormat.daysCharacters: Int
    get() {
        return when (this) {
            VGSDateFormat.mmyyyy, VGSDateFormat.mmyy -> 0
            VGSDateFormat.mmddyyyy, VGSDateFormat.ddmmyyyy, VGSDateFormat.yyyymmdd -> 1
        }
    }

internal val VGSDateFormat.monthCharacters: Int
    get() = 2

internal val VGSDateFormat.yearCharacters: Int
    get() {
        return when (this) {
            VGSDateFormat.mmyyyy, VGSDateFormat.mmddyyyy, VGSDateFormat.ddmmyyyy, VGSDateFormat.yyyymmdd -> 4
            VGSDateFormat.mmyy -> 2
        }
    }

internal val VGSDateFormat.dividerCharacters: Int
    get() {
        return when (this) {
            VGSDateFormat.mmyyyy, VGSDateFormat.mmyy -> 1
            VGSDateFormat.mmddyyyy, VGSDateFormat.ddmmyyyy, VGSDateFormat.yyyymmdd -> 2
        }
    }

internal val VGSDateFormat.size: Int
    get() {
        return this.daysCharacters + this.monthCharacters + this.yearCharacters + this.dividerCharacters
    }
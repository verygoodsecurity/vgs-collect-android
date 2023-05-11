package com.verygoodsecurity.vgscollect.view.date.formatter

import com.verygoodsecurity.vgscollect.util.extension.digits
import java.text.SimpleDateFormat
import java.util.*

// TODO: Move to correct place
internal data class DateComponentsString(
    var day: String,
    var month: String,
    var year: String
) {
    val isDayValid: Boolean
        get() = day.isNotEmpty()
    val isMonthValid: Boolean
        get() = month.isNotEmpty()
    val isYearValid: Boolean
        get() = year.isNotEmpty()
}

// TODO: Move to correct place
fun String.prefix(count: Int): Pair<String, String> {
    var result = this
    val prefix =
        if (result.length >= count) {
            val value = result.take(count)
            result = result.removePrefix(value)
            value
        } else {
            val value = result
            result = ""
            value
        }
    return Pair(prefix, result)
}

internal sealed interface IDateFormat {
    val format: String
}

sealed class VGSDateFormat : IDateFormat {

    class mmddyyyy : VGSDateFormat() {
        override val format: String = "MM-dd-yyyy"
    }
    class ddmmyyyy : VGSDateFormat() {
        override val format: String = "dd-MM-yyyy"
    }
    class yyyymmdd : VGSDateFormat() {
        override val format = "yyyy-MM-dd"
    }

    internal val daysCharacters = 2
    internal val monthCharacters = 2
    internal val yearCharacters = 4
    internal val dividerCharacters = 2
    internal val size: Int = daysCharacters + monthCharacters + yearCharacters + dividerCharacters

    // Date format pattern used to display in the text field
    internal val patternItem = '#'

    internal val pattern: String
        get() {
            val patternItem = patternItem.toString()
            return format
                .replace("M", patternItem, true)
                .replace("y", patternItem, true)
                .replace("d", patternItem, true)
        }

    fun stringFromDate(date: Date): String? {
        val sDateFormat = SimpleDateFormat(format, Locale.US)
        sDateFormat.isLenient = false
        return sDateFormat.format(date)
    }

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

    internal fun dateComponentsString(input: String): DateComponentsString {
        // Get only digits
        var digitsInput = input.digits

        val result = DateComponentsString("", "", "")

        when (this) {
            // Get month, day and year
            is mmddyyyy -> {
                // Get month
                val monthPrefix = digitsInput.prefix(monthCharacters)
                digitsInput = monthPrefix.second
                result.month = monthPrefix.first

                // Get day
                if (result.isMonthValid) {
                    val dayPrefix = digitsInput.prefix(daysCharacters)
                    digitsInput = dayPrefix.second
                    result.day = dayPrefix.first

                    // Get year
                    if (result.isDayValid) {
                        val yearPrefix = digitsInput.prefix(yearCharacters)
                        result.year = yearPrefix.first
                    }
                }
            }
            // Get day, month and year
            is ddmmyyyy -> {
                // Get day
                val dayPrefix = digitsInput.prefix(daysCharacters)
                digitsInput = dayPrefix.second
                result.day = dayPrefix.first

                // Get month
                if (result.isDayValid) {
                    val monthPrefix = digitsInput.prefix(monthCharacters)
                    digitsInput = monthPrefix.second
                    result.month = monthPrefix.first

                    // Get year
                    if (result.isMonthValid) {
                        val yearPrefix = digitsInput.prefix(yearCharacters)
                        result.year = yearPrefix.first
                    }
                }
            }
            // Get year, month and day
            is yyyymmdd -> {
                // Get year
                val yearPrefix = digitsInput.prefix(yearCharacters)
                digitsInput = yearPrefix.second
                result.year = yearPrefix.first

                // Get month
                if (result.isYearValid) {
                    val monthPrefix = digitsInput.prefix(monthCharacters)
                    digitsInput = monthPrefix.second
                    result.month = monthPrefix.first

                    // Get day
                    if (result.isMonthValid) {
                        val dayPrefix = digitsInput.prefix(daysCharacters)
                        result.day = dayPrefix.first
                    }
                }
            }
        }
        return result
    }

    companion object {

        // Default format
        val default = mmddyyyy()
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
                mmddyyyy().format -> mmddyyyy()
                ddmmyyyy().format -> ddmmyyyy()
                yyyymmdd().format -> yyyymmdd()
                else -> null
            }
        }
    }
}

/*


internal enum class VGSDateFormat(val displayFormat: String) {
    MM_DD_YYYY("MM-dd-yyyy"),
    DD_MM_YYYY("dd-MM-yyyy"),
    YYYY_MM_DD("yyyy-MM-dd");

    internal val daysCharacters = 2
    internal val monthCharacters = 2
    internal val yearCharacters = 4
    internal val dividerCharacters = 2

    internal val size: Int = daysCharacters + monthCharacters + yearCharacters + dividerCharacters

    // Date format pattern used to display in the text field
    internal val formatPatternItem = '#'

    internal val formatPattern: String
        get() {
            val patternItem = formatPatternItem.toString()
            return displayFormat
                .replace("M", patternItem, true)
                .replace("y", patternItem, true)
                .replace("d", patternItem, true)
        }

    fun format(date: VGSDate, divider: String): String {
        // Day and month values
        val dayString = String.format("%02d", date.day)
        val monthString = String.format("%02d", date.month)

        // Return the string of the date based on the format
        return when (this) {
            MM_DD_YYYY -> {
                "${monthString}${divider}${dayString}${divider}${date.year}"
            }
            DD_MM_YYYY -> {
                "${dayString}${divider}${monthString}${divider}${date.year}"
            }
            YYYY_MM_DD -> {
                "${date.year}${divider}${monthString}${divider}${dayString}"
            }
        }
    }

    fun dateComponentsString(input: String): DateComponentsString {
        // Get only digits
        var digitsInput = input.digits

        val result = DateComponentsString("", "", "")

        when (this) {
            // Get month, day and year
            MM_DD_YYYY -> {
                // Get month
                val monthPrefix = digitsInput.prefix(monthCharacters)
                digitsInput = monthPrefix.second
                result.month = monthPrefix.first

                // Get day
                if (result.isMonthValid) {
                    val dayPrefix = digitsInput.prefix(daysCharacters)
                    digitsInput = dayPrefix.second
                    result.day = dayPrefix.first

                    // Get year
                    if (result.isDayValid) {
                        val yearPrefix = digitsInput.prefix(yearCharacters)
                        result.year = yearPrefix.first
                    }
                }
            }
            // Get day, month and year
            DD_MM_YYYY -> {
                // Get day
                val dayPrefix = digitsInput.prefix(daysCharacters)
                digitsInput = dayPrefix.second
                result.day = dayPrefix.first

                // Get month
                if (result.isDayValid) {
                    val monthPrefix = digitsInput.prefix(monthCharacters)
                    digitsInput = monthPrefix.second
                    result.month = monthPrefix.first

                    // Get year
                    if (result.isMonthValid) {
                        val yearPrefix = digitsInput.prefix(yearCharacters)
                        result.year = yearPrefix.first
                    }
                }
            }
            // Get year, month and day
            YYYY_MM_DD -> {
                // Get year
                val yearPrefix = digitsInput.prefix(yearCharacters)
                digitsInput = yearPrefix.second
                result.year = yearPrefix.first

                // Get month
                if (result.isYearValid) {
                    val monthPrefix = digitsInput.prefix(monthCharacters)
                    digitsInput = monthPrefix.second
                    result.month = monthPrefix.first

                    // Get day
                    if (result.isMonthValid) {
                        val dayPrefix = digitsInput.prefix(daysCharacters)
                        result.day = dayPrefix.first
                    }
                }
            }
        }

        return result
    }

    fun dateFromInput(input: String?): VGSDate? {
        // Make sure if is a valid input string
        if (input.isNullOrEmpty()) {
            return null
        }
        // Get only digits
        val digitsInput = input.digits
        // Check the amount of chars per date component are correct
        val expectedCount = daysCharacters + monthCharacters + yearCharacters
        if (digitsInput.length != expectedCount) {
            return null
        }
        // Format the date
        when (this) {
            // Get month, day and year
            MM_DD_YYYY -> return try {
                // Get month, day and year
                val month = digitsInput.take(monthCharacters).toInt()
                val day =
                    digitsInput.substring(monthCharacters, monthCharacters + daysCharacters).toInt()
                val year = digitsInput.takeLast(yearCharacters).toInt()
                // Try to create the date
                VGSDate.createDate(day, month, year)
            } catch (e: Exception) {
                null
            }
            // Get day, month and year
            DD_MM_YYYY -> return try {
                // Get day, month and year
                val day = digitsInput.take(daysCharacters).toInt()
                val month =
                    digitsInput.substring(daysCharacters, daysCharacters + monthCharacters).toInt()
                val year = digitsInput.takeLast(yearCharacters).toInt()
                // Try to create the date
                VGSDate.createDate(day, month, year)
            } catch (e: Exception) {
                null
            }
            // Get year, month and day
            YYYY_MM_DD -> return try {
                // Get year, month and day
                val year = digitsInput.take(yearCharacters).toInt()
                val month =
                    digitsInput.substring(yearCharacters, yearCharacters + monthCharacters).toInt()
                val day = digitsInput.takeLast(daysCharacters).toInt()
                // Try to create the date
                VGSDate.createDate(day, month, year)
            } catch (e: Exception) {
                null
            }
        }
    }

    companion object {

        // Default format
        val default = MM_DD_YYYY
        const val defaultDivider = "-"

        fun findDividerInInput(input: String?): String? {
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

        fun parsePatternToDateFormat(pattern: String?): VGSDateFormat? {
            // Make sure if is a valid pattern string
            if (pattern.isNullOrEmpty()) {
                return null
            }

            // Find divider
            val divider = findDividerInInput(pattern) ?: return null

            // Replace dividers with the default
            return when (pattern.replace(divider, defaultDivider, true)) {
                MM_DD_YYYY.displayFormat -> MM_DD_YYYY
                DD_MM_YYYY.displayFormat -> DD_MM_YYYY
                YYYY_MM_DD.displayFormat -> YYYY_MM_DD
                else -> null
            }
        }
    }
}
 */
package com.verygoodsecurity.vgscollect.view.card.formatter.date

import android.text.Editable
import android.widget.EditText
import com.verygoodsecurity.vgscollect.util.extension.digits
import com.verygoodsecurity.vgscollect.util.extension.replaceIgnoreFilters
import com.verygoodsecurity.vgscollect.view.date.*
import com.verygoodsecurity.vgscollect.view.date.DateRangeFormat
import java.util.regex.Pattern

internal class StrictDateRangeFormatter(
    private val source: EditText? = null
) : BaseDateFormatter() {

    //region - Properties
    private var dateFormat = DateRangeFormat.MM_DD_YYYY
    private var mode: DatePickerMode = DatePickerMode.INPUT
    private var divider = DateRangeFormat.divider
    private var runtimeData = ""
    private var isDeleteAction = false
    private var skipStep = false
    private var tempString: CharSequence = ""
    private var cacheDay = ""
    private var cacheMonth = ""
    private var cacheYear = ""
    //endregion

    //region - Overrides
    override fun setMode(mode: DatePickerMode) {
        this.mode = mode
    }

    override fun getMask(): String = dateFormat.formatPattern

    override fun setMask(mask: String) {
        val parsedFormat = DateRangeFormat.parsePatternToDateFormat(mask)
        if (parsedFormat != null) {
            dateFormat = parsedFormat
        }
    }
    //endregion

    //region - TextWatcher implementation
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        isDeleteAction = after < count

        if (isDeleteAction) {
            // Detect string which will be removed
            s?.substring(
                start + after,
                start + count
            )?.also {
                // Skip deleting if divider will be removed, and divider not the last character
                skipStep = it.contains(divider) && s.lastIndexOf(divider) + 1 != s.length
            }?.also {
                tempString =
                    when {
                        !skipStep -> s
                        s.toString() == it -> ""
                        else -> it.replace(divider, "").run {
                            s.replace(this.toRegex(), "")
                        }
                    }
            }
        } else {
            tempString = s ?: ""
            skipStep = false
        }
    }

    override fun onTextChanged(str: CharSequence, start: Int, before: Int, count: Int) {
        val changedStr = if (skipStep) tempString else str

        runtimeData = when {
            changedStr.isEmpty() && runtimeData.isEmpty() -> {
                cacheDay = ""
                cacheMonth = ""
                cacheYear = ""
                ""
            }
            changedStr.isEmpty() -> "".also {
                cacheDay = it
                cacheMonth = it
                cacheYear = it
            }
            dateFormat == DateRangeFormat.MM_DD_YYYY -> {
                generateMMDDYYYY(str)
            }
            dateFormat == DateRangeFormat.DD_MM_YYYY -> {
                generateDDMMYYYY(str)
            }
            dateFormat == DateRangeFormat.YYYY_MM_DD -> {
                generateYYYYMMDD(str)
            }
            else -> "".also {
                cacheMonth = it
                cacheYear = it
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {
        if (mode == DatePickerMode.INPUT) {
            s?.apply {
                if (s.toString() != runtimeData) {
                    replaceIgnoreFilters(0, s.length, runtimeData)
                }
            }
        }
    }
    //endregion

    //region - Private methods
    private fun generateMMDDYYYY(str: CharSequence): String {
        // Get date components
        val dateComponents = dateComponentsString(str.toString())
        // Format month
        val formattedMonth = formatMonth(dateComponents.month)
        // Only month is valid
        if (dateComponents.isMonthValid && !dateComponents.isDayValid && !dateComponents.isYearValid) {
            cacheDay = ""
            cacheYear = ""
            return when {
                isDeleteAction -> formattedMonth
                formattedMonth == "1" -> formattedMonth
                formattedMonth == "0" -> formattedMonth
                else -> formattedMonth + divider
            }
        } else {
            // Format day and year
            val formattedDay = formatDay(dateComponents.day)
            val formattedYear = formatYear(dateComponents.year)
            // Create result
            var result = ""
            if (formattedMonth.isNotEmpty()) {
                result = formattedMonth
            }
            if (formattedDay.isNotEmpty()) {
                result += divider + formattedDay
            }
            if (formattedYear.isNotEmpty()) {
                result += divider + formattedYear
            }
            return result
        }
    }

    private fun generateDDMMYYYY(str: CharSequence): String {
        // Get date components
        val dateComponents = dateComponentsString(str.toString())
        // Format day
        val formattedDay = formatDay(dateComponents.day)
        // Only day is valid
        if (dateComponents.isDayValid && !dateComponents.isMonthValid && !dateComponents.isYearValid) {
            cacheMonth = ""
            cacheYear = ""
            return when {
                isDeleteAction -> formattedDay
                formattedDay == "3" -> formattedDay
                formattedDay == "2" -> formattedDay
                formattedDay == "1" -> formattedDay
                formattedDay == "0" -> formattedDay
                else -> formattedDay + divider
            }
        } else {
            // Format month and year
            val formattedMonth = formatMonth(dateComponents.month)
            val formattedYear = formatYear(dateComponents.year)
            // Create result
            var result = ""
            if (formattedDay.isNotEmpty()) {
                result = formattedDay
            }
            if (formattedMonth.isNotEmpty()) {
                result += divider + formattedMonth
            }
            if (formattedYear.isNotEmpty()) {
                result += divider + formattedYear
            }
            return result
        }
    }

    private fun generateYYYYMMDD(str: CharSequence): String {
        // Get date components
        val dateComponents = dateComponentsString(str.toString())
        // Format year
        val formattedYear = formatYear(dateComponents.year)
        // Only year is valid
        if (dateComponents.isYearValid && !dateComponents.isMonthValid && !dateComponents.isDayValid) {
            cacheMonth = ""
            cacheDay = ""
            return when {
                isDeleteAction -> formattedYear
                formattedYear.length < dateFormat.yearCharacters -> formattedYear
                else -> formattedYear + divider
            }
        } else {
            // Format month and day
            val formattedMonth = formatMonth(dateComponents.month)
            val formattedDay = formatDay(dateComponents.day)
            // Create result
            var result = ""
            if (formattedYear.isNotEmpty()) {
                result = formattedYear
            }
            if (formattedMonth.isNotEmpty()) {
                result += divider + formattedMonth
            }
            if (formattedDay.isNotEmpty()) {
                result += divider + formattedDay
            }
            return result
        }
    }

    private fun formatDay(day: String): String {
        val isValid = DayFormatter.isValidDay(day)
        return when {
            day.isEmpty() -> "".also { cacheDay = "" }
            isValid -> day.also { cacheDay = it }
            isDeleteAction -> "".also { moveCursorToEndOfDay() }
            else -> DayFormatter.validateDay(day) ?: cacheDay
        }
    }

    private fun formatMonth(month: String): String {
        val isValid = MonthFormatter.isValidMonth(month)
        return when {
            month.isEmpty() -> "".also { cacheMonth = "" }
            isValid -> month.also { cacheMonth = it }
            isDeleteAction -> "".also { moveCursorToEndOfMonth() }
            else -> MonthFormatter.validateMonth(month) ?: cacheMonth
        }
    }

    private fun formatYear(year: String): String {
        val isValid = YearFormatter.isValidYear(year)
        return when {
            year.isEmpty() -> "".also { cacheYear = "" }
            isValid -> year.also { cacheYear = it }
            isDeleteAction -> "".also { moveCursorToEndOfYear() }
            else -> cacheYear
        }
    }

    private fun moveCursorToEndOfDay() {
        when (dateFormat) {
            DateRangeFormat.MM_DD_YYYY,
            DateRangeFormat.YYYY_MM_DD -> {
                val index = if (runtimeData.isNotEmpty()) {
                    runtimeData.length - 1
                } else {
                    0
                }
                source?.setSelection(index)
            }
            DateRangeFormat.DD_MM_YYYY -> {
                source?.setSelection(0)
            }
        }
    }

    private fun moveCursorToEndOfMonth() {
        when (dateFormat) {
            DateRangeFormat.MM_DD_YYYY -> {
                source?.setSelection(0)
            }
            DateRangeFormat.DD_MM_YYYY,
            DateRangeFormat.YYYY_MM_DD -> {
                val index = if (runtimeData.isNotEmpty()) {
                    runtimeData.length - 1
                } else {
                    0
                }
                source?.setSelection(index)
            }
        }
    }

    private fun moveCursorToEndOfYear() {
        when (dateFormat) {
            DateRangeFormat.MM_DD_YYYY,
            DateRangeFormat.DD_MM_YYYY -> {
                val index = if (runtimeData.isNotEmpty()) {
                    runtimeData.length - 1
                } else {
                    0
                }
                source?.setSelection(index)
            }
            DateRangeFormat.YYYY_MM_DD -> {
                source?.setSelection(0)
            }
        }
    }

    private fun dateComponentsString(input: String): DateComponentsString {
        val result = DateComponentsString("", "", "")

        // Get only digits
        var digits = input.digits
        when (dateFormat) {
            // Get month, day and year
            DateRangeFormat.MM_DD_YYYY -> {
                // Get month
                result.month = digits.take(dateFormat.monthCharacters)
                digits = digits.removePrefix(result.month)
                // Get day
                result.day = digits.take(dateFormat.daysCharacters)
                digits = digits.removePrefix(result.day)
                // Get year
                result.year = digits.take(dateFormat.yearCharacters)
            }
            // Get day, month and year
            DateRangeFormat.DD_MM_YYYY -> {
                // Get day
                result.day = digits.take(dateFormat.daysCharacters)
                digits = digits.removePrefix(result.day)
                // Get month
                result.month = digits.take(dateFormat.monthCharacters)
                digits = digits.removePrefix(result.month)
                // Get year
                result.year = digits.take(dateFormat.yearCharacters)
            }
            // Get year, month and day
            DateRangeFormat.YYYY_MM_DD -> {
                // Get year
                result.year = digits.take(dateFormat.yearCharacters)
                digits = digits.removePrefix(result.year)
                // Get month
                result.month = digits.take(dateFormat.monthCharacters)
                digits = digits.removePrefix(result.month)
                // Get day
                result.day = digits.take(dateFormat.daysCharacters)
            }
        }
        return result
    }
    //endregion

    //region - Inner classes
    private data class DateComponentsString(
        var day: String,
        var month: String,
        var year: String
    ) {
        val isDayValid = day.isNotEmpty()
        val isMonthValid = month.isNotEmpty()
        val isYearValid = year.isNotEmpty()
    }

    private class DayFormatter {

        companion object {

            private val patternDay = Pattern.compile("^([0123]|0[1-9]|1\\d|2\\d|3[01])\$")

            fun isValidDay(day: String): Boolean {
                return patternDay.matcher(day).matches()
            }

            fun validateDay(day: String): String? {
                return try {
                    val dayInt = day.toInt()
                    if (dayInt in 1..30) {
                        return String.format("%02d", dayInt)
                    } else {
                        return null
                    }
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    private class MonthFormatter {

        companion object {

            private val patternMounts = Pattern.compile("^([10]|0[1-9]|1[012])\$")

            fun isValidMonth(month: String): Boolean {
                return patternMounts.matcher(month).matches()
            }

            fun validateMonth(month: String): String? {
                return try {
                    val monthInt = month.toInt()
                    if (monthInt in 1..12) {
                        return String.format("%02d", monthInt)
                    } else {
                        return null
                    }
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    private class YearFormatter {

        companion object {

            private val patternYear = Pattern.compile("^((1|19|19\\d|19\\d\\d)|(2|20|20\\d|20\\d\\d))\$")

            fun isValidYear(year: String): Boolean {
                return patternYear.matcher(year).matches()
            }
        }
    }
    //endregion
}
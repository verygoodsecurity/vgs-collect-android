package com.verygoodsecurity.vgscollect.view.card.formatter.date.base

import android.text.Editable
import android.widget.EditText
import com.verygoodsecurity.vgscollect.view.card.formatter.date.BaseDateFormatter
import com.verygoodsecurity.vgscollect.view.date.DateFormat
import com.verygoodsecurity.vgscollect.view.date.DatePickerMode
import com.verygoodsecurity.vgscollect.view.date.yearCharacters
import java.util.regex.Pattern

internal class StrictDateRangeFormatter(
    var dateFormatter: DateFormat,
    var pickerMode: DatePickerMode,
    private val source: EditText? = null
) : BaseDateFormatter() {

    override fun setMode(mode: DatePickerMode) {
        // This method is not required in new implementation
    }

    override fun setMask(mask: String) {
        // This method is not required in new implementation
    }

    override fun getMask(): String {
        return dateFormatter.formatPattern
    }

    //region - Properties
    private var divider = "/"
    private var runtimeData = ""
    private var isDeleteAction = false
    private var skipStep = false
    private var tempString: CharSequence = ""
    private var cacheDay = ""
    private var cacheMonth = ""
    private var cacheYear = ""
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
            dateFormatter == DateFormat.MM_DD_YYYY -> {
                generateMMDDYYYY(str)
            }
            dateFormatter == DateFormat.DD_MM_YYYY -> {
                generateDDMMYYYY(str)
            }
            dateFormatter == DateFormat.YYYY_MM_DD -> {
                generateYYYYMMDD(str)
            }
            dateFormatter == DateFormat.MM_YYYY -> {
                // TODO: Pending implementation
                ""
            }
            dateFormatter == DateFormat.MM_YYYY -> {
                // TODO: Pending implementation
                ""
            }
            else -> "".also {
                cacheMonth = it
                cacheYear = it
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {
        if (pickerMode == DatePickerMode.INPUT) {
            s?.apply {
                if (s.toString() != runtimeData) {
                    replace(0, s.length, runtimeData)
                }
            }
        }
    }
    //endregion

    //region - Private methods
    private fun generateMMDDYYYY(str: CharSequence): String {
        // Get date components
        val dateComponents = dateFormatter.dateComponentsString(str.toString())
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
        val dateComponents = dateFormatter.dateComponentsString(str.toString())
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
        val dateComponents = dateFormatter.dateComponentsString(str.toString())
        // Format year
        val formattedYear = formatYear(dateComponents.year)
        // Only year is valid
        if (dateComponents.isYearValid && !dateComponents.isMonthValid && !dateComponents.isDayValid) {
            cacheMonth = ""
            cacheDay = ""
            return when {
                isDeleteAction -> formattedYear
                formattedYear.length < dateFormatter.yearCharacters -> formattedYear
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
        when (dateFormatter) {
            DateFormat.MM_DD_YYYY,
            DateFormat.YYYY_MM_DD -> {
                val index = if (runtimeData.isNotEmpty()) {
                    runtimeData.length - 1
                } else {
                    0
                }
                source?.setSelection(index)
            }
            DateFormat.DD_MM_YYYY -> {
                source?.setSelection(0)
            }
            DateFormat.MM_YY, DateFormat.MM_YYYY -> {
                // Do nothing
            }
        }
    }

    private fun moveCursorToEndOfMonth() {
        when (dateFormatter) {
            DateFormat.MM_DD_YYYY,
            DateFormat.MM_YYYY,
            DateFormat.MM_YY -> {
                source?.setSelection(0)
            }
            DateFormat.DD_MM_YYYY,
            DateFormat.YYYY_MM_DD -> {
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
        when (dateFormatter) {
            DateFormat.MM_DD_YYYY,
            DateFormat.DD_MM_YYYY,
            DateFormat.MM_YYYY,
            DateFormat.MM_YY-> {
                val index = if (runtimeData.isNotEmpty()) {
                    runtimeData.length - 1
                } else {
                    0
                }
                source?.setSelection(index)
            }
            DateFormat.YYYY_MM_DD -> {
                source?.setSelection(0)
            }
        }
    }
    //endregion
}


internal class DayFormatter {

    companion object {

        private val patternDay = Pattern.compile("^([0123]|0[1-9]|1[0-9]|2[0-9]|3[01])\$")

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

internal class MonthFormatter {

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

internal class YearFormatter {

    companion object {

        private val patternYear = Pattern.compile("^((1|1[9]|1[9]\\d|1[9]\\d\\d)|(2|2[0]|2[0]\\d|2[0]\\d\\d))\$")

        fun isValidYear(year: String): Boolean {
            return patternYear.matcher(year).matches()
        }
    }
}
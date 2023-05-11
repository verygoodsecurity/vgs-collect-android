package com.verygoodsecurity.vgscollect.view.date.formatter

import android.text.Editable
import android.widget.EditText
import com.verygoodsecurity.vgscollect.view.date.DatePickerMode
import java.util.regex.Pattern

internal class StrictDateRangeFormatter(
    override var dateFormatter: VGSDateFormat,
    override var pickerMode: DatePickerMode,
    private val divider: String,
    private val source: EditText? = null
) : BaseDateRangeFormatter(), DateRangePickerFormatter {

    //region - Properties
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
                // handle String state before removing
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
            dateFormatter is VGSDateFormat.mmddyyyy -> {
                generateMMDDYYYY(str)
            }
            dateFormatter is VGSDateFormat.ddmmyyyy -> {
                generateDDMMYYYY(str)
            }
            dateFormatter is VGSDateFormat.yyyymmdd -> {
                generateYYYYMMDD(str)
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
        val dateComponents = dateFormatter.dateComponentsString(str.toString())
        val formattedMonth = formatMonth(dateComponents.month)
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
            val formattedDay = formatDay(dateComponents.day)
            val formattedYear = formatYear(dateComponents.year)

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
        val dateComponents = dateFormatter.dateComponentsString(str.toString())
        val formattedDay = formatDay(dateComponents.day)
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
            val formattedMonth = formatMonth(dateComponents.month)
            val formattedYear = formatYear(dateComponents.year)

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
        val dateComponents = dateFormatter.dateComponentsString(str.toString())
        val formattedYear = formatYear(dateComponents.year)
        if (dateComponents.isYearValid && !dateComponents.isMonthValid && !dateComponents.isDayValid) {
            cacheMonth = ""
            cacheDay = ""
            return when {
                isDeleteAction -> formattedYear
                formattedYear.length < dateFormatter.yearCharacters -> formattedYear
                else -> formattedYear + divider
            }
        } else {
            val formattedMonth = formatMonth(dateComponents.month)
            val formattedDay = formatDay(dateComponents.day)

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
        val isValidDay = DayFormatter.isValidDay(day)
        return when {
            day.isEmpty() -> "".also { cacheDay = "" }
            isValidDay -> day.also { cacheDay = it }
            isDeleteAction -> "".also { moveCursorToEnd_D() }
            else -> DayFormatter.validateDay(day) ?: cacheDay
        }
    }

    private fun formatMonth(month: String): String {
        val isValidMonth = MonthFormatter.isValidMonth(month)
        return when {
            month.isEmpty() -> "".also { cacheMonth = "" }
            isValidMonth -> month.also { cacheMonth = it }
            isDeleteAction -> "".also { moveCursorToEnd_M() }
            else -> MonthFormatter.validateMonth(month) ?: cacheMonth
        }
    }

    private fun formatYear(year: String): String {
        val isValid = YearFormatter.isValidYear(year)
        return when {
            year.isEmpty() -> "".also { cacheYear = "" }
            isValid -> year.also { cacheYear = it }
            isDeleteAction -> "".also { moveCursorToEnd_Year() }
            else -> cacheYear
        }
    }

    private fun moveCursorToEnd_D() {
        when (dateFormatter) {
            is VGSDateFormat.mmddyyyy,
            is VGSDateFormat.yyyymmdd -> {
                val index = if (runtimeData.isNotEmpty()) {
                    runtimeData.length - 1
                } else {
                    0
                }
                source?.setSelection(index)
            }
            is VGSDateFormat.ddmmyyyy -> {
                source?.setSelection(0)
            }
        }
    }

    private fun moveCursorToEnd_M() {
        when (dateFormatter) {
            is VGSDateFormat.mmddyyyy -> {
                source?.setSelection(0)
            }
            is VGSDateFormat.ddmmyyyy,
            is VGSDateFormat.yyyymmdd -> {
                val index = if (runtimeData.isNotEmpty()) {
                    runtimeData.length - 1
                } else {
                    0
                }
                source?.setSelection(index)
            }
        }
    }

    private fun moveCursorToEnd_Year() {
        when (dateFormatter) {
            is VGSDateFormat.mmddyyyy,
            is VGSDateFormat.ddmmyyyy -> {
                val index = if (runtimeData.isNotEmpty()) {
                    runtimeData.length - 1
                } else {
                    0
                }
                source?.setSelection(index)
            }
            is VGSDateFormat.yyyymmdd -> {
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
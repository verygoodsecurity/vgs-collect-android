package com.verygoodsecurity.vgscollect.view.date.formatter

import android.text.Editable
import android.widget.EditText
import com.verygoodsecurity.vgscollect.core.model.VGSDate
import com.verygoodsecurity.vgscollect.view.card.formatter.date.StrictDateFormatter
import com.verygoodsecurity.vgscollect.view.date.DatePickerMode
import java.util.regex.Pattern

internal class StrictDateRangeFormatter(
    override var dateFormatter: VGSDateFormat,
    override var pickerMode: DatePickerMode,
    private val divider: String,
    private val source: EditText? = null
) : BaseDateRangeFormatter(), DateRangePickerFormatter {

    //region: Properties
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
                tempString = when {                                // handle String state before removing
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
            dateFormatter == VGSDateFormat.MM_DD_YYYY -> {
                generateMMDDYYYY(str)
            }
            dateFormatter == VGSDateFormat.DD_MM_YYYY -> {
                ""
            }
            dateFormatter == VGSDateFormat.YYYY_MM_DD -> {
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
                result +=  divider + formattedYear
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

    private fun moveCursorToEnd_D() {

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
            isDeleteAction -> "" //.also { moveCursorToEnd_Year() }
            else -> cacheYear
        }
    }

    private fun moveCursorToEnd_M() {
        when (dateFormatter) {
            VGSDateFormat.MM_DD_YYYY -> {
                source?.setSelection(0)
            }
            VGSDateFormat.DD_MM_YYYY -> {
                source?.setSelection(runtimeData.length - 1)
            }
            VGSDateFormat.YYYY_MM_DD -> {
                source?.setSelection(runtimeData.length - 1)
            }
        }
    }
    //endregion
}

internal class DayFormatter {

    companion object {

        private val patternDay = Pattern.compile("^([1-9]|[12]\\d|3[01])\$")

        fun isValidDay(day: String) : Boolean {
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

        fun isValidMonth(month: String) : Boolean {
            return patternMounts.matcher(month).matches()
        }

        fun validateMonth(month: String): String? {
            return when (month) {
                "2" -> "02"
                "3" -> "03"
                "4" -> "04"
                "5" -> "05"
                "6" -> "06"
                "7" -> "07"
                "8" -> "08"
                "9" -> "09"
                "10" -> "10"
                "11" -> "11"
                "12" -> "12"
                else -> null
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
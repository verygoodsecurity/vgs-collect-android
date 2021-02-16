package com.verygoodsecurity.vgscollect.view.card.formatter.date

import android.text.Editable
import android.widget.EditText
import com.verygoodsecurity.vgscollect.view.date.DatePickerMode
import java.util.regex.Pattern

internal open class StrictDateFormatter(
        private val source: EditText? = null
) : BaseDateFormatter() {

    companion object {
        private const val DATE_PATTERN = "MM/yyyy"
        private const val DATE_FORMAT = "##/####"

        private const val YEAR_FULL_REGEX = "^((1|1[9]|1[9]\\d|1[9]\\d\\d)|(2|2[0]|2[0]\\d|2[0]\\d\\d))\$"
        private const val YEAR_REGEX = "^(\\d|\\d\\d)\$"
        private const val MONTH_REGEX = "^([10]|0[1-9]|1[012])\$"

        private fun validateMonth(m: String): String? {
            return when (m) {
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

    protected open val yearLongRegex: String
        get() = YEAR_FULL_REGEX
    protected open val yearShortRegex: String
        get() = YEAR_REGEX

    private val patternMounts = Pattern.compile(MONTH_REGEX)
    private var patternYear: Pattern? = null

    private var mask: String = DATE_FORMAT
    private var mode: DatePickerMode = DatePickerMode.INPUT

    private var divider = "/"

    private var runtimeData: String = ""

    private var mounthIndex: Int = -1
    private var yearIndex: Int = -1
    private var year: String? = null

    private var isDeleteAction = false

    private var cacheMonth = ""
    private var cacheYear = ""

    init {
        calculateMounthLimitations(DATE_PATTERN)
        calculateYearLimitations(DATE_PATTERN)
    }

    override fun afterTextChanged(s: Editable?) {
        if (mode == DatePickerMode.INPUT) {
            s?.apply {
                if (s.toString() != runtimeData) {
                    replace(0, s.length, runtimeData)
                }
            }
        }
    }

    private var skipStep = false
    private var tempString: CharSequence = ""
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        isDeleteAction = after < count

        if (isDeleteAction) {
            s?.substring(                                       // detect string which will be removed
                    start + after,
                    start + count
            )?.also {
                skipStep = it.contains(divider) &&              // skip deleting if divider will be removed
                        s.lastIndexOf(divider) + 1 != s.length  // and divider not the last character
            }?.also {
                tempString = when {                                // handle String state before removing
                    !skipStep -> s
                    s.toString() == it -> ""
                    else -> it.replace("/", "").run {
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
                cacheMonth = ""
                cacheYear = ""
                ""
            }
            changedStr.isEmpty() -> "".also {
                cacheMonth = it
                cacheYear = it
            }
            mounthIndex > yearIndex -> generateYYMM(changedStr)
            yearIndex > mounthIndex -> generateMMYY(changedStr)
            else -> "".also {
                cacheMonth = it
                cacheYear = it
            }
        }
    }

    private fun generateMMYY(str: CharSequence): String {
        val splittedTime = str.split("\\D".toRegex())
        val formattedMonth = formatMonth(splittedTime.getOrNull(0) ?: "")

        return if (splittedTime.size == 1) {
            cacheYear = ""
            when {
                isDeleteAction -> formattedMonth
                formattedMonth == "1" -> formattedMonth
                formattedMonth == "0" -> formattedMonth
                else -> formattedMonth + divider
            }
        } else {
            val formattedYear = formatYear(splittedTime.getOrNull(1) ?: "")
            if (formattedYear.isEmpty() && formattedMonth.isEmpty()) "" else formattedMonth + divider + formattedYear
        }
    }

    private fun generateYYMM(str: CharSequence): String {
        val splittedTime = str.split("\\D".toRegex())
        val formattedYear = formatYear(splittedTime.getOrNull(0) ?: "")

        return if (splittedTime.size == 1) {
            cacheMonth = ""
            when {
                isDeleteAction -> formattedYear
                formattedYear.length < year!!.length -> formattedYear
                else -> formattedYear + divider
            }
        } else {
            val formattedMonth = formatMonth(splittedTime.getOrNull(1) ?: "")
            if (formattedYear.isEmpty() && formattedMonth.isEmpty()) "" else formattedYear + divider + formattedMonth
        }
    }

    private fun formatYear(year: String): String {
        val isValid = patternYear?.matcher(year)?.matches() ?: true

        val newM = when {
            year.isEmpty() -> "".also { cacheYear = "" }
            isValid -> year.also { cacheYear = it }
            isDeleteAction -> "".also { moveCursorToEnd_Year() }
            else -> cacheYear
        }

        return newM
    }

    private fun moveCursorToEnd_Year() {
        if (yearIndex > mounthIndex) {
            source?.setSelection(runtimeData.length - 1)
        } else {
            source?.setSelection(0)
        }
    }

    private fun moveCursorToEnd_M() {
        if (yearIndex < mounthIndex) {
            source?.setSelection(runtimeData.length - 1)
        } else {
            source?.setSelection(0)
        }
    }

    private fun formatMonth(month: String): String {
        val isValid = patternMounts.matcher(month).matches()
        val newM = when {
            month.isEmpty() -> "".also { cacheMonth = "" }
            isValid -> month.also { cacheMonth = it }
            isDeleteAction -> "".also { moveCursorToEnd_M() }
            else -> validateMonth(month) ?: cacheMonth
        }

        return newM
    }

    override fun setMode(mode: DatePickerMode) {
        this.mode = mode
    }

    override fun getMask(): String = mask

    override fun setMask(mask: String) {
        this.mask = mask
                .replace("M", "#", true)
                .replace("y", "#", true)
                .also {
                    divider = it.replace("#", "")
                }
        calculateMounthLimitations(mask)
        calculateYearLimitations(mask)
    }

    private fun calculateYearLimitations(datePattern: String) {
        yearIndex = if (datePattern.contains("yyyy")) {
            year = "yyyy"
            patternYear = Pattern.compile(yearLongRegex)
            datePattern.indexOf("yyyy")
        } else {
            year = "yy"
            patternYear = Pattern.compile(yearShortRegex)
            datePattern.indexOf("yy")
        }
    }

    private fun calculateMounthLimitations(datePattern: String) {
        val mounth = when {
            datePattern.contains("MMMM") -> "MM"
            datePattern.contains("MMM") -> "MM"
            datePattern.contains("MM") -> "MM"
            datePattern.contains("M") -> "MM"
            else -> null
        }

        mounthIndex = mounth?.run { datePattern.indexOf(this) } ?: -1
    }
}
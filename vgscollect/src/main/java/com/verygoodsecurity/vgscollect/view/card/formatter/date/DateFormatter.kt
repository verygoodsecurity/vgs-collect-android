package com.verygoodsecurity.vgscollect.view.card.formatter.date

import android.text.Editable
import android.text.TextWatcher
import com.verygoodsecurity.vgscollect.view.date.DatePickerMode
import java.lang.StringBuilder
import java.util.regex.Pattern

internal open class DateFormatter : TextWatcher, DatePickerFormatter {

    companion object {
        private const val DATE_PATTERN = "MM/yyyy"
        private const val DATE_FORMAT = "##/####"

        private const val YEAR_FULL_REGEX = "^((1|1[9]|1[9]\\d|1[9]\\d\\d)|(2|2[0]|2[0]\\d|2[0]\\d\\d))\$"
        private const val YEAR_REGEX = "^(\\d|\\d\\d)\$"
        private const val MONTH_REGEX = "^([10]|0[1-9]|1[012])\$"

        private const val NUMBER_REGEX = "[^\\d]"
        private const val MASK_ITEM = '#'

        private fun validateMonth(m:String):String {
            return when(m) {
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
                else -> m
            }
        }
    }

    protected open val yearLongRegex: String
        get() = YEAR_FULL_REGEX
    protected open val yearShortRegex: String
        get() = YEAR_REGEX

    private val patternMounts = Pattern.compile(MONTH_REGEX)
    private var patternYear:Pattern? = null

    private var mask:String = DATE_FORMAT
    private var mode:DatePickerMode = DatePickerMode.INPUT

    var runtimeData:String = ""

    var mounthIndex:Int = -1
    var mounth:String? = null
    var yearIndex:Int = -1
    var year:String? = null

    init {
        calculateMounthLimitations(DATE_PATTERN)
        calculateYearLimitations(DATE_PATTERN)
    }

    override fun afterTextChanged(s: Editable?) {
        if(mode == DatePickerMode.INPUT) {
            s?.apply {
                if(s.toString() != runtimeData) {
                    replace(0, s.length, runtimeData)
                }
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(str: CharSequence, start: Int, before: Int, count: Int) {
        var formattedText = clearTextFromSymbols(str)

        formattedText = formatYear(formattedText)
        runtimeData = formatMonth(formattedText)
    }

    private fun clearTextFromSymbols(str: CharSequence): String {
        val text = str.toString().replace(Regex(NUMBER_REGEX), "")
        val textCount = text.length

        val builder = StringBuilder()
        var indexSpace = 0

        repeat(textCount) { charIndex ->
            val maskIndex = charIndex+indexSpace

            if(maskIndex < mask.length) {
                val maskChar = mask[maskIndex]
                val char = text[charIndex]

                if (maskChar == MASK_ITEM) {
                    builder.append(char)
                } else {
                    indexSpace += 1
                    builder.append(maskChar)
                    if (char.isDigit()) {
                        builder.append(char)
                    }
                }
            }
        }

        return builder.toString()
    }

    private fun substringDateBlock(indexStart:Int, indexEnd:Int, runtimeData: String):String {
        val count = runtimeData.length

        return when {
            count in (indexStart + 1) until indexEnd -> runtimeData.substring(indexStart, count)
            indexStart in 0 until count -> runtimeData.substring(indexStart, indexEnd)
            else -> ""
        }
    }

    private fun formatMonth(runtimeData: String): String {
        val month = substringDateBlock(mounthIndex, mounthIndex+mounth!!.length, runtimeData)

        if(month.isEmpty()) return runtimeData

        val isValid = patternMounts.matcher(month).matches()
        return if(isValid) {
            return runtimeData
        } else {
            handleMonths(month, runtimeData)
        }
    }

    private fun handleMonths(month: String, runtimeData: String): String {
        val formattedMonth = validateMonth(month)

        val isValid = patternMounts.matcher(formattedMonth).matches()
        return if(isValid) {
            runtimeData.substring(0, runtimeData.length-1) + formattedMonth
        } else {
            runtimeData.substring(0, runtimeData.length-1)
        }
    }

    private fun formatYear(runtimeData: String): String {
        val year = substringDateBlock(yearIndex, yearIndex+year!!.length, runtimeData)

        if(year.isEmpty()) return runtimeData

        val isValid = patternYear?.matcher(year)?.matches()?:true
        return if(isValid) {
            return runtimeData
        } else {
            runtimeData.substring(0, runtimeData.length-1)
        }
    }

    override fun setMode(mode: DatePickerMode) {
        this.mode = mode
    }

    override fun getMask(): String = mask

    override fun setMask(mask: String) {
        this.mask = mask
            .replace("M", "#", true)
            .replace("y", "#", true)

        calculateMounthLimitations(mask)
        calculateYearLimitations(mask)
    }

    private fun calculateYearLimitations(datePattern: String) {
        yearIndex = if(datePattern.contains("yyyy")) {
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
        mounth = when {
            datePattern.contains("MMMM") -> "MM"
            datePattern.contains("MMM") -> "MM"
            datePattern.contains("MM") -> "MM"
            datePattern.contains("M") -> "MM"
            else -> null
        }

        mounthIndex = mounth?.run { datePattern.indexOf(this) }?:-1
    }
}
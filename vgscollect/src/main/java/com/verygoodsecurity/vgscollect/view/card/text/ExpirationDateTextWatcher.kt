package com.verygoodsecurity.vgscollect.view.card.text

import android.text.Editable
import android.text.TextWatcher
import java.util.regex.Pattern

/** @suppress */
@Deprecated("Use DateFormatter or ExpirationDateFormatter class")
class ExpirationDateTextWatcher(
    private val datePattern:String = "MM/yy"
): TextWatcher {

    companion object {
        private const val YEAR_FULL_REGEX = "^([2]|2[0]|20[23]|20[23][0123456789])\$"   //using for credit cards years from 2020 to 2039
        private const val YEAR_SHORT_REGEX = "^([23]|[123]\\d)\$"
        private const val MONTH_REGEX = "^([10]|0[1-9]|1[012])\$"
        private const val DAY_REGEX = "^([1230]|0[1-9]|1[012]|[12]\\d|3[01])\$"
        private const val DIVIDER_REGEX = "^[^0-9]\$"

        private const val DEFAULT_PATTERN = "MM/yy"

        private const val YYYY = "yyyy"
        private const val YY = "yy"
        private const val MM = "MM"
        private const val DD = "dd"
    }

    private val mountPattern = MONTH_REGEX
    private val mountPositionStart:Int
    private val mountPositionEnd:Int

    private val dayPattern = DAY_REGEX
    private val dayPositionStart:Int
    private val dayPositionEnd:Int

    private val yearPattern:String
    private val yearPositionStart:Int
    private val yearPositionEnd:Int

    private val totalSymbols:Int
    private val totalDigits:Int

    init {
        val symbolLength = datePattern.replace(YYYY, "")
            .replace(YY, "")
            .replace(DD, "")
            .replace(MM, "")
            .length


        dayPositionStart = datePattern.lastIndexOf(DD)
        dayPositionEnd = if(dayPositionStart >= 0) {
            dayPositionStart+2
        } else {
            -1
        }


        mountPositionStart = datePattern.lastIndexOf(MM)
        mountPositionEnd = if(mountPositionStart >= 0) {
            mountPositionStart+2
        } else {
            -1
        }


        val fullYPosition = datePattern.lastIndexOf(YYYY)
        val YPosition = datePattern.lastIndexOf(YY)
        yearPattern = if(fullYPosition != -1) {
            yearPositionStart = fullYPosition
            yearPositionEnd = if(yearPositionStart >= 0) {
                yearPositionStart+4
            } else {
                -1
            }
            YEAR_FULL_REGEX
        } else {
            yearPositionStart = YPosition
            yearPositionEnd = if(yearPositionStart > 0) {
                yearPositionStart+2
            } else {
                -1
            }
            YEAR_SHORT_REGEX
        }


        totalSymbols = datePattern.length
        totalDigits = totalSymbols - symbolLength
    }

    private val patternMounts = Pattern.compile(mountPattern)
    private val patternYear = Pattern.compile(yearPattern)
    private val patternDay = Pattern.compile(dayPattern)
    private val patternDivider = Pattern.compile(DIVIDER_REGEX)


    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun afterTextChanged(s: Editable) {
        if (!isInputCorrect(s, totalSymbols)) {
            s.replace(0, s.length,
                build(s)
            )
        }
    }

    private fun build(s: Editable): CharSequence? {
        if(datePattern.length < s.length) {
            return s.substring(0, s.length-1)
        }

        when (s.length-1) {
            in dayPositionStart until dayPositionEnd -> {
                val last = s.substring(dayPositionStart).toIntOrNull()
                when {
                    last == null -> return s.substring(0, s.length-1)
                    last in 4..9 -> {
                        val str = s.substring(0,s.length-1)+"0"+s.substring(s.length-1,s.length)
                        return str
                    }
                    else -> return s.substring(0, s.length-1)
                }
            }
            in mountPositionStart until mountPositionEnd -> {
                val last = s.substring(mountPositionStart).toIntOrNull()
                when {
                    last == null -> return s.substring(0, s.length-1)
                    last in 2..9 -> {
                        val str = s.substring(0,s.length-1)+"0"+s.substring(s.length-1,s.length)
                        return str
                    }
                    else -> return s.substring(0, s.length-1)
                }
            }
            in yearPositionStart until yearPositionEnd -> {

            }
            else -> {
                val divider = datePattern.substring(s.length-1,s.length)
                val str = s.substring(0,s.length-1)+divider+s.substring(s.length-1,s.length)
                return str
            }
        }

        return s.substring(0, s.length-1)
    }

    private fun isInputCorrect(s: Editable, totalSymbols:Int):Boolean {
        var isCorrect = s.length <= totalSymbols

        var isMonthStep: Boolean
        var isDayStep: Boolean
        var isYearStep: Boolean
        var isDividerStep: Boolean

        var year = ""
        var month = ""
        var day = ""
        var divider = ""

        for (i in s.indices) {
            when (s.length-1) {
                in dayPositionStart until dayPositionEnd -> {
                    day = s.substring(dayPositionStart)

                    isDayStep = true
                    isMonthStep = false
                    isYearStep = false
                    isDividerStep = false
                }
                in mountPositionStart until mountPositionEnd -> {
                    month = s.substring(mountPositionStart)

                    isDayStep = false
                    isMonthStep = true
                    isYearStep = false
                    isDividerStep = false
                }
                in yearPositionStart until yearPositionEnd -> {
                    year = s.substring(yearPositionStart)

                    isDayStep = false
                    isMonthStep = false
                    isYearStep = true
                    isDividerStep = false
                }
                else -> {
                    divider = s[s.length-1].toString()
                    isDayStep = false
                    isMonthStep = false
                    isYearStep = false
                    isDividerStep = true
                }
            }

            isCorrect = when {
                isDayStep -> isCorrect && patternDay.matcher(day).matches()
                isMonthStep -> isCorrect && patternMounts.matcher(month).matches()
                isYearStep -> isCorrect && patternYear.matcher(year).matches()
                isDividerStep -> isCorrect && patternDivider.matcher(divider).matches()
                else -> isCorrect
            }
        }

        return isCorrect
    }
}
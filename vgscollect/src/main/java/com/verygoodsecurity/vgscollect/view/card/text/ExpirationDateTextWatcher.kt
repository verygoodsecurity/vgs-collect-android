package com.verygoodsecurity.vgscollect.view.card.text

import android.text.Editable
import android.text.TextWatcher
import java.util.regex.Pattern

class ExpirationDateTextWatcher(
    private val pattern:String = "MM/yy"
): TextWatcher {

    private val mountPattern = "^([10]|0[1-9]|1[012])\$"
    private val mountPositionStart:Int
    private val mountPositionEnd:Int

    private val dayPattern = "^([1230]|0[1-9]|1[012]|[12]\\d|3[01])\$"
    private val dayPositionStart:Int
    private val dayPositionEnd:Int

    private val yearPattern:String
    private val yearPositionStart:Int
    private val yearPositionEnd:Int

    private val totalSymbols:Int
    private val totalDigits:Int

    init {
        val symbolLength = pattern.replace("yyyy", "")
            .replace("yy", "")
            .replace("dd", "")
            .replace("MM", "")
            .length


        dayPositionStart = pattern.lastIndexOf("dd")
        dayPositionEnd = if(dayPositionStart >= 0) {
            dayPositionStart+2
        } else {
            -1
        }


        mountPositionStart = pattern.lastIndexOf("MM")
        mountPositionEnd = if(mountPositionStart >= 0) {
            mountPositionStart+2
        } else {
            -1
        }


        val fullYPosition = pattern.lastIndexOf("yyyy")
        val YPosition = pattern.lastIndexOf("yy")
        yearPattern = if(fullYPosition != -1) {
            yearPositionStart = fullYPosition
            yearPositionEnd = if(yearPositionStart >= 0) {
                yearPositionStart+4
            } else {
                -1
            }
            "^([2]|2[0]|20[23]|20[23][0123456789])\$"
        } else {
            yearPositionStart = YPosition
            yearPositionEnd = if(yearPositionStart > 0) {
                yearPositionStart+2
            } else {
                -1
            }
            "^([23]|[123]\\d)\$"
        }


        totalSymbols = pattern.length
        totalDigits = totalSymbols - symbolLength
    }

    private val patternMounts = Pattern.compile(mountPattern)
    private val patternYear = Pattern.compile(yearPattern)
    private val patternDay = Pattern.compile(dayPattern)
    private val patternDivider = Pattern.compile("^[^0-9]\$")


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
        if(pattern.length < s.length) {
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
                val divider = pattern.substring(s.length-1,s.length)
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
package com.verygoodsecurity.vgscollect.view.text.validation.card

import android.text.InputFilter
import android.text.Spanned
import java.util.*
import java.util.regex.Pattern

class DateValiateFilter: InputFilter {
    override fun filter(
        source : CharSequence?,
        start : Int,
        end: Int,
        dest : Spanned?,
        dstart : Int,
        dend: Int
    ): CharSequence {

        source.toString().split("/")


        if(end == 5) {
            for (i in start until end) {
                val checkMe = source.toString()
                val pattern = Pattern.compile("(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$")
                val matcher = pattern.matcher(checkMe)
                val valid = matcher.matches()
                return if (valid) {
                    source ?: ""
                } else {
                    ""
                }
            }
            return ""
        } else {
            return source ?: ""
        }
    }
}
package com.verygoodsecurity.vgscollect.view.card.text

import android.text.InputFilter
import android.text.Spanned
import java.util.regex.Pattern

/** @suppress */
class CVCValidateFilter: InputFilter {
    override fun filter(
        source : CharSequence?,
        start : Int,
        end: Int,
        dest : Spanned?,
        dstart : Int,
        dend: Int
    ): CharSequence {
        for (i in start until end) {
            val checkMe = source.toString()
            val pattern = Pattern.compile("[1234567890]*")
            val matcher = pattern.matcher(checkMe)
            val valid = matcher.matches()
            return if (valid) {
                source?:""
            } else {
                ""
            }
        }
        return ""
    }
}
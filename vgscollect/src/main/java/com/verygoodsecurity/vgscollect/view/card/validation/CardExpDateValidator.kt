package com.verygoodsecurity.vgscollect.view.card.validation

import java.util.regex.Pattern

class CardExpDateValidator:VGSValidator {
    private val m = Pattern.compile("^([01]|0[1-9]|1[012])[\\/]((19|20)\\d\\d|(2)\\d|(19))\$")

    override fun isValid(content: String?): Boolean {
        val str = content?.trim()
        return !str.isNullOrEmpty() &&
                m.matcher(str).matches()
    }
}
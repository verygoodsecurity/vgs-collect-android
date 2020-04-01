package com.verygoodsecurity.vgscollect.view.card.validation

import java.util.regex.Pattern

/** @suppress */
class InfoValidator:VGSValidator {
    private val m = Pattern.compile(".*?")

    override fun isValid(content: String?): Boolean {
        val str = content?.trim()
        return !str.isNullOrEmpty() &&
                m.matcher(str).matches()
    }
}
package com.verygoodsecurity.vgscollect.view.card.validation

import java.util.regex.Pattern

class InfoValidator:VGSValidator {
    private val m = Pattern.compile(".*?")
    override fun clearRules() {}

    override fun addRule(regex: String) {}

    override fun isValid(content: String?): Boolean {
        val str = content?.trim()
        return !str.isNullOrEmpty() &&
                m.matcher(str).matches()
    }
}
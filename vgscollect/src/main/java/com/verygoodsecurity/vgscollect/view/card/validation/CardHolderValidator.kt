package com.verygoodsecurity.vgscollect.view.card.validation

import java.util.regex.Pattern

class CardHolderValidator:VGSValidator {
    private val m = Pattern.compile("^[a-zA-Z0-9 ,]+\$")      //only symbols  -  "^[\\p{L}\\s'.-]+\$"
    override fun clearRules() {}

    override fun addRule(regex: String) {}

    override fun isValid(content: String?): Boolean {
        return m.matcher(content).matches()
    }
}
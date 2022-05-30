package com.verygoodsecurity.vgscollect.view.card.validation

import java.util.regex.Pattern

/** @suppress */
class RegexValidator(
    regex: String? = null
) : VGSValidator {
    private var m: Pattern? = null

    init {
        if (!regex.isNullOrEmpty()) {
            m = Pattern.compile(regex)
        }
    }

    override fun isValid(content: String?): Boolean {
        return m?.run {
            !content.isNullOrEmpty() && m!!.matcher(content).matches()
        } ?: true
    }

    internal fun setRegex(regex: String) {
        m = Pattern.compile(regex)
    }
}
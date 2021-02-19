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
            val str = content?.trim()
            val s = !str.isNullOrEmpty() &&
                    m!!.matcher(str).matches()
            s
        } ?: true
    }

    internal fun setRegex(regex: String) {
        m = Pattern.compile(regex)
    }
}
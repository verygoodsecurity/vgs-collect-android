package com.verygoodsecurity.vgscollect.view.card.validation

import com.verygoodsecurity.vgscollect.view.card.validation.rules.VGSValidationResultListener
import java.util.regex.Pattern

/** @suppress */
class RegexValidator(
    regex: String? = null,
    private val listener: VGSValidationResultListener? = null
) : VGSValidator {
    private var m: Pattern? = null

    init {
        if (!regex.isNullOrEmpty()) {
            m = Pattern.compile(regex)
        }
    }

    override fun isValid(content: String?): Boolean {
       val result =  m?.run {
            !content.isNullOrEmpty() && m!!.matcher(content).matches()
        } ?: true
        listener?.onResult(result)
        return result
    }

    internal fun setRegex(regex: String) {
        m = Pattern.compile(regex)
    }
}
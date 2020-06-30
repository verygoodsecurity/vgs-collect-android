package com.verygoodsecurity.vgscollect.view.card.validation.payment.brand

import com.verygoodsecurity.vgscollect.view.card.validation.VGSValidator
import java.util.regex.Pattern

/** @suppress */
class RegexValidator(
    regex:String = ""
): VGSValidator {
    private val m = Pattern.compile(regex)

    override fun isValid(content: String?): Boolean {
        return !content.isNullOrEmpty() &&
                m.matcher(content).matches()
    }
}

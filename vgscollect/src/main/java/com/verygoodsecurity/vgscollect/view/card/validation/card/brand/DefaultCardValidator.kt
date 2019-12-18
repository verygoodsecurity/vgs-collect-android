package com.verygoodsecurity.vgscollect.view.card.validation.card.brand

import com.verygoodsecurity.vgscollect.view.card.validation.VGSValidator
import java.util.regex.Pattern

class DefaultCardValidator(
    regex:String
): VGSValidator {
    private val m = Pattern.compile(regex)

    override fun isValid(content: String?): Boolean {
        return !content.isNullOrEmpty() &&
                m.matcher(content).matches()
    }
}
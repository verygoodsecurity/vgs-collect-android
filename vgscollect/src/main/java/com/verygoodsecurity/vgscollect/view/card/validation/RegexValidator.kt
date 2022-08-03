package com.verygoodsecurity.vgscollect.view.card.validation

import com.verygoodsecurity.vgscollect.view.card.validation.rules.VGSValidationResultListener
import java.util.regex.Pattern

/** @suppress */
class RegexValidator(
    private val regex: String,
    private val listener: VGSValidationResultListener? = null
) : VGSValidator {

    private var pattern: Pattern = Pattern.compile(regex)

    override fun isValid(content: String): Boolean {
        val result = if (content.isEmpty()) true else pattern.matcher(content).matches()
        listener?.onResult(result)
        return result
    }
}
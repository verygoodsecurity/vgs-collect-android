package com.verygoodsecurity.vgscollect.view.card.validation

import com.verygoodsecurity.vgscollect.view.card.validation.rules.VGSValidationResultListener
import java.util.regex.Pattern

/** @suppress */
class RegexValidator(
    internal val value: String,
    private val listener: VGSValidationResultListener? = null
) : VGSValidator {

    private var pattern: Pattern = Pattern.compile(value)

    override fun isValid(content: String): Boolean {
        val result = if (content.isEmpty()) true else pattern.matcher(content).matches()
        listener?.onResult(result)
        return result
    }
}
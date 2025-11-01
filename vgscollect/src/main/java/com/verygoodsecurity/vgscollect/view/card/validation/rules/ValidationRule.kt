package com.verygoodsecurity.vgscollect.view.card.validation.rules

import com.verygoodsecurity.vgscollect.view.card.validation.CheckSumValidator
import com.verygoodsecurity.vgscollect.view.card.validation.LengthMatchValidator
import com.verygoodsecurity.vgscollect.view.card.validation.LengthValidator
import com.verygoodsecurity.vgscollect.view.card.validation.RegexValidator

internal const val MIN_LENGTH = 1
internal const val MAX_LENGTH = 256

/**
 * The base class for all validation rules.
 */
open class ValidationRule internal constructor(
    internal val algorithm: CheckSumValidator?,
    internal val regex: RegexValidator?,
    internal val length: LengthValidator?,
    internal val lengthMatch: LengthMatchValidator?,
) {

    internal fun isAnyRulePresent(): Boolean {
        return algorithm != null || regex != null || length != null || lengthMatch != null
    }
}
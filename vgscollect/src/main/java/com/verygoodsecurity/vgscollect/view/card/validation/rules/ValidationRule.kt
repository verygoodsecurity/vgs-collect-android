package com.verygoodsecurity.vgscollect.view.card.validation.rules

import com.verygoodsecurity.vgscollect.view.card.validation.LengthValidator
import com.verygoodsecurity.vgscollect.view.card.validation.RegexValidator

open class ValidationRule internal constructor(
    internal val regexValidator: RegexValidator?,
    internal val lengthValidator:LengthValidator?
)
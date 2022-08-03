package com.verygoodsecurity.vgscollect.view.card.validation

import com.verygoodsecurity.vgscollect.view.card.validation.payment.ChecksumAlgorithm
import com.verygoodsecurity.vgscollect.view.card.validation.payment.brand.LuhnCheckSumValidator

class CheckSumValidator(
    internal val value: ChecksumAlgorithm,
    errorMsg: String = DEFAULT_ERROR_MSG
) : VGSValidator {

    private val validator: LuhnCheckSumValidator? = when (value) {
        ChecksumAlgorithm.LUHN -> LuhnCheckSumValidator(errorMsg)
        ChecksumAlgorithm.ANY -> LuhnCheckSumValidator(errorMsg)
        ChecksumAlgorithm.NONE -> null
    }

    override fun isValid(content: String): String? {
        return validator?.isValid(content)
    }

    internal companion object {

        internal const val DEFAULT_ERROR_MSG = "LUHN_ALGORITHM_CHECK_VALIDATION_ERROR"
    }
}
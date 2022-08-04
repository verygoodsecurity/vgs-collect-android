package com.verygoodsecurity.vgscollect.view.card.validation

import com.verygoodsecurity.vgscollect.view.card.validation.payment.ChecksumAlgorithm
import com.verygoodsecurity.vgscollect.view.card.validation.payment.brand.LuhnCheckSumValidator

class CheckSumValidator(
    internal val value: ChecksumAlgorithm,
    override val errorMsg: String = DEFAULT_ERROR_MSG
) : VGSValidator {

    private val validator: LuhnCheckSumValidator? = when (value) {
        ChecksumAlgorithm.LUHN -> LuhnCheckSumValidator(errorMsg)
        ChecksumAlgorithm.ANY -> LuhnCheckSumValidator(errorMsg)
        ChecksumAlgorithm.NONE -> null
    }

    override fun isValid(content: String) = validator?.isValid(content) ?: true

    internal companion object {

        internal const val DEFAULT_ERROR_MSG = "LUHN_ALGORITHM_CHECK_VALIDATION_ERROR"
    }
}
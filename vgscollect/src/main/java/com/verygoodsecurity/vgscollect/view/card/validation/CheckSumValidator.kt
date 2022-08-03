package com.verygoodsecurity.vgscollect.view.card.validation

import com.verygoodsecurity.vgscollect.view.card.validation.payment.ChecksumAlgorithm
import com.verygoodsecurity.vgscollect.view.card.validation.payment.brand.LuhnCheckSumValidator
import com.verygoodsecurity.vgscollect.view.card.validation.rules.VGSValidationResultListener

class CheckSumValidator(
    internal val value: ChecksumAlgorithm,
    private val listener: VGSValidationResultListener? = null
) : VGSValidator {

    private val validator: LuhnCheckSumValidator? = when (value) {
        ChecksumAlgorithm.LUHN -> LuhnCheckSumValidator()
        ChecksumAlgorithm.ANY -> LuhnCheckSumValidator()
        ChecksumAlgorithm.NONE -> null
    }

    override fun isValid(content: String): Boolean {
        val result = validator?.isValid(content) ?: true
        listener?.onResult(result)
        return result
    }
}
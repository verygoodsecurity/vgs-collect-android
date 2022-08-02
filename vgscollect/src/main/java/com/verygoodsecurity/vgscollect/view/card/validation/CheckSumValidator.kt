package com.verygoodsecurity.vgscollect.view.card.validation

import com.verygoodsecurity.vgscollect.view.card.validation.payment.ChecksumAlgorithm
import com.verygoodsecurity.vgscollect.view.card.validation.payment.brand.LuhnCheckSumValidator
import com.verygoodsecurity.vgscollect.view.card.validation.rules.VGSValidationResultListener

class CheckSumValidator(
    algorithm: ChecksumAlgorithm,
    private val listener: VGSValidationResultListener? = null
) : VGSValidator {

    private val validationList:Array<VGSValidator> = when(algorithm) {
        ChecksumAlgorithm.LUHN -> arrayOf(
            LuhnCheckSumValidator()
        )
        ChecksumAlgorithm.ANY -> arrayOf(
            LuhnCheckSumValidator()
        )
        else -> arrayOf()
    }

    override fun isValid(content: String?): Boolean {
        var isValid = true
        for(checkSumValidator in validationList) {
            isValid =  isValid && checkSumValidator.isValid(content)
        }
        listener?.onResult(isValid)
        return isValid
    }
}
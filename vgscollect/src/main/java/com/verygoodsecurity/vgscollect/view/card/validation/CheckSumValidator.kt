package com.verygoodsecurity.vgscollect.view.card.validation

import com.verygoodsecurity.vgscollect.view.card.validation.payment.ChecksumAlgorithm
import com.verygoodsecurity.vgscollect.view.card.validation.payment.brand.LuhnCheckSumValidator

class CheckSumValidator(algorithm: ChecksumAlgorithm) : VGSValidator {
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
        return isValid
    }
}
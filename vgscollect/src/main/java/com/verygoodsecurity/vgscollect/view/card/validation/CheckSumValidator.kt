package com.verygoodsecurity.vgscollect.view.card.validation

import com.verygoodsecurity.vgscollect.view.card.validation.bank.ChecksumAlgorithm
import com.verygoodsecurity.vgscollect.view.card.validation.bank.brand.LuhnCheckSumDelegate

class CheckSumValidator(algorithm: ChecksumAlgorithm) : VGSValidator {
    private val validationList:Array<VGSValidator> = when(algorithm) {
        ChecksumAlgorithm.LUHN -> arrayOf(
            LuhnCheckSumDelegate()
        )
        ChecksumAlgorithm.ANY -> arrayOf(
            LuhnCheckSumDelegate()
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
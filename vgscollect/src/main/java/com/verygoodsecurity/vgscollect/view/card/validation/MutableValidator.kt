package com.verygoodsecurity.vgscollect.view.card.validation

interface MutableValidator:VGSValidator {
    fun clearRules()
    fun addRule(validator: VGSValidator)
}
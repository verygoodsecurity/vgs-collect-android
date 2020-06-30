package com.verygoodsecurity.vgscollect.view.card.validation

/** @suppress */
interface MutableValidator:VGSValidator {
    fun clearRules()
    fun addRule(validator: VGSValidator)
}
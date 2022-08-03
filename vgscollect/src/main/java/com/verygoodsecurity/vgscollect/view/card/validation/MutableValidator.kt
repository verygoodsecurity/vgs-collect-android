package com.verygoodsecurity.vgscollect.view.card.validation

/** @suppress */
interface MutableValidator : VGSValidator {

    fun addRule(validator: VGSValidator)

    fun clearRules()
}
package com.verygoodsecurity.vgscollect.view.card.validation

/** @suppress */
interface MuttableValidator:VGSValidator {
    fun clearRules()
    fun addRule(validator:VGSValidator)
}
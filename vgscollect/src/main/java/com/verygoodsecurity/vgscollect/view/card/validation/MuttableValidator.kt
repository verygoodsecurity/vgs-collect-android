package com.verygoodsecurity.vgscollect.view.card.validation

interface MuttableValidator:VGSValidator {
    fun clearRules()
    fun addRule(regex:String)
}
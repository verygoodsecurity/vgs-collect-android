package com.verygoodsecurity.vgscollect.view.card.validation

interface VGSValidator {

    fun clearRules()
    fun addRule(regex:String)

    fun isValid(content:String?):Boolean
}
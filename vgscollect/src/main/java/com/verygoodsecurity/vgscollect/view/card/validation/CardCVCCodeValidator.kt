package com.verygoodsecurity.vgscollect.view.card.validation

class CardCVCCodeValidator:VGSValidator {
    override fun isValid(content: String?): Boolean {
        val data:Int? =  content?.trim()?.toIntOrNull()
        return data != null && data >= 3
    }
}
package com.verygoodsecurity.vgscollect.view.card.validation

/** @suppress */
class CardCVCCodeValidator(
    private val length:Int = 3
):VGSValidator {
    override fun isValid(content: String?): Boolean {
        val data:Int? =  content?.trim()?.toIntOrNull()
        return data != null && content.length >= length
    }
}
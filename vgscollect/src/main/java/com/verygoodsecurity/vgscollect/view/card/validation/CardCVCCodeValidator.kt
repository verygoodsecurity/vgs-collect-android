package com.verygoodsecurity.vgscollect.view.card.validation

/** @suppress */
class CardCVCCodeValidator(
    private val cvcLength:Array<Int> = arrayOf(3,4)
):VGSValidator {
    override fun isValid(content: String?): Boolean {
        val data:Int? =  content?.trim()?.toIntOrNull()
        return data != null && cvcLength.contains(content.length)
    }
}
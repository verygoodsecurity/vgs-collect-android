package com.verygoodsecurity.vgscollect.view.card.validation

/** @suppress */
interface VGSValidator {

    val errorMsg: String

    fun isValid(content: String): Boolean
}
package com.verygoodsecurity.vgscollect.view.card.validation

import com.verygoodsecurity.vgscollect.view.card.validation.rules.VGSValidationResultListener

/** @suppress */
internal class LengthValidator : VGSValidator {

    private val length: Array<Int>
    private val listener: VGSValidationResultListener?

    constructor(length: Int, listener: VGSValidationResultListener? = null) {
        this.length = arrayOf(length)
        this.listener = listener
    }

    constructor(length: Array<Int>, listener: VGSValidationResultListener? = null) {
        this.length = length
        this.listener = listener
    }

    override fun isValid(content: String?): Boolean {
        val result = !content.isNullOrEmpty() && length.contains(content.length)
        listener?.onResult(result)
        return result
    }
}
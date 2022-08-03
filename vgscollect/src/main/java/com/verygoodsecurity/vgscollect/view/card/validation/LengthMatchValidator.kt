package com.verygoodsecurity.vgscollect.view.card.validation

import com.verygoodsecurity.vgscollect.view.card.validation.rules.VGSValidationResultListener

/** @suppress */
internal class LengthMatchValidator : VGSValidator {

    internal val values: Array<Int>

    private val listener: VGSValidationResultListener?

    constructor(length: Int, listener: VGSValidationResultListener? = null) {
        this.values = arrayOf(length)
        this.listener = listener
    }

    constructor(length: Array<Int>, listener: VGSValidationResultListener? = null) {
        this.values = length
        this.listener = listener
    }

    override fun isValid(content: String): Boolean {
        val result = content.isNotEmpty() && values.contains(content.length)
        listener?.onResult(result)
        return result
    }
}
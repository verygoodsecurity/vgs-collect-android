package com.verygoodsecurity.vgscollect.view.card.validation

/** @suppress */
internal class LengthValidator : VGSValidator {

    private val length:Array<Int>

    constructor(length:Int) {
        this.length = arrayOf(length)
    }

    constructor(length: Array<Int>) {
        this.length = length
    }

    override fun isValid(content: String?): Boolean {
        return !content.isNullOrEmpty() && length.contains(content.length)
    }
}
package com.verygoodsecurity.vgscollect.view.card.validation

/** @suppress */
class InfoValidator : VGSValidator {

    override fun isValid(content: String?): Boolean {
        return !content?.trim().isNullOrEmpty()
    }
}
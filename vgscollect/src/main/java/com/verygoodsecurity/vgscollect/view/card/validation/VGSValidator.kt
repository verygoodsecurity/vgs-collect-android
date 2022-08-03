package com.verygoodsecurity.vgscollect.view.card.validation

/** @suppress */
interface VGSValidator {

    /**
     * Check if content valid.
     *
     * @return error message if not valid, null otherwise.
     */
    fun isValid(content: String): String?
}
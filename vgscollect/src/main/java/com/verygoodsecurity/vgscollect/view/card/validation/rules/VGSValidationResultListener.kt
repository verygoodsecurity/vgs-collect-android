package com.verygoodsecurity.vgscollect.view.card.validation.rules

/**
 * A listener for receiving the result of a validation.
 */
fun interface VGSValidationResultListener {

    /**
     * Called when a validation has completed.
     *
     * @param isSuccessful Whether the validation was successful.
     */
    fun onResult(isSuccessful: Boolean)
}
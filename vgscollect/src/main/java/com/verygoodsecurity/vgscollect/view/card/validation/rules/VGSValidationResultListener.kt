package com.verygoodsecurity.vgscollect.view.card.validation.rules

fun interface VGSValidationResultListener {

    /** Invoke when validation complete. */
    fun onResult(isSuccessful: Boolean)
}
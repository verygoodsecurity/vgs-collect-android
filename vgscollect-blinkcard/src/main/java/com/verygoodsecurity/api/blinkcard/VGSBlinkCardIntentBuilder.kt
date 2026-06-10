package com.verygoodsecurity.api.blinkcard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.microblink.blinkcard.ux.contract.BlinkCardScanActivitySettings

/**
 * Used to create Intent instance which is required to start scanner.
 *
 * @param activity the Activity context used to create Intent
 * @param settings BlinkCardScanActivitySettings instance which controls scanner behavior. This is required and must be provided in the constructor.
 */
class VGSBlinkCardIntentBuilder(
    private val activity: Activity,
    private var settings: BlinkCardScanActivitySettings
) {

    private var ccFieldName: String? = null
    private var cvcFieldName: String? = null
    private var expDateFieldName: String? = null
    private var cHolderFieldName: String? = null

    /**
     * Sets card number field name. The name is used for data mapping after scan.
     *
     * @param fieldName the name of the card number field
     */
    fun setCardNumberFieldName(fieldName: String?) = this.apply {
        this.ccFieldName = fieldName
    }

    /**
     * Sets expiration date field name. The name is used for data mapping after scan.
     *
     * @param fieldName the name of the expiration date field
     */
    fun setExpirationDateFieldName(fieldName: String?) = this.apply {
        this.expDateFieldName = fieldName
    }

    /**
     * Sets cvc field name. The name is used for data mapping after scan.
     *
     * @param fieldName the name of the cvc field
     */
    fun setCVCFieldName(fieldName: String?) = this.apply {
        this.cvcFieldName = fieldName
    }

    /**
     * Sets cardholder field name. The name is used for data mapping after scan.
     *
     * @param fieldName the name of the cardholder field
     */
    fun setCardHolderFieldName(fieldName: String?) = this.apply {
        this.cHolderFieldName = fieldName
    }

    /**
     * Returns ready to use Intent instance which is required to start scanner.
     */
    fun build(): Intent = with(Intent(activity, ScanActivity::class.java)) {
        putExtras(Bundle().apply {
            putParcelable(SCAN_ACTIVITY_SETTINGS, settings)
            putString(CARD_NUMBER, ccFieldName)
            putString(CVC, cvcFieldName)
            putString(CARD_HOLDER, cHolderFieldName)
            putString(EXP_DATE, expDateFieldName)
        })
    }
}
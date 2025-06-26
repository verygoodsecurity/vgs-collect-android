package com.verygoodsecurity.api.blinkcard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.annotation.StyleRes
import com.verygoodsecurity.api.blinkcard.ScanActivity.Companion.CARD_NUMBER
import com.verygoodsecurity.api.blinkcard.ScanActivity.Companion.CVC
import com.verygoodsecurity.api.blinkcard.ScanActivity.Companion.CARD_HOLDER
import com.verygoodsecurity.api.blinkcard.ScanActivity.Companion.EXP_DATE
import com.verygoodsecurity.api.blinkcard.ScanActivity.Companion.SHOW_INTRO_DIALOG
import com.verygoodsecurity.api.blinkcard.ScanActivity.Companion.SHOW_ONBOARDING_INFO_DIALOG
import com.verygoodsecurity.api.blinkcard.ScanActivity.Companion.STYLE_RES_ID

/**
 * Used to create Intent instance which is required to start scanner.
 */
@Suppress("unused")
class VGSBlinkCardIntentBuilder(
    private val activity: Activity
) {

    private var styleId: Int? = null
    private var ccFieldName: String? = null
    private var cvcFieldName: String? = null
    private var expDateFieldName: String? = null
    private var cHolderFieldName: String? = null
    private var showIntroductionDialog: Boolean = true
    private var showOnboardingInfoDialog: Boolean = true

    /**
     * Allows to customize UI by configuring style resource for labels, appearance, etc..
     *
     * @param resId resource id
     */
    fun setOverlayViewStyle(@StyleRes resId: Int): VGSBlinkCardIntentBuilder = apply {
        styleId = resId
    }

    /**
     * Sets card number field name. The name is used for data mapping after scan.
     *
     * @param fieldName the name of the card number field
     */
    fun setCardNumberFieldName(fieldName: String?): VGSBlinkCardIntentBuilder = apply {
        this.ccFieldName = fieldName
    }

    /**
     * Sets expiration date field name. The name is used for data mapping after scan.
     *
     * @param fieldName the name of the expiration date field
     */
    fun setExpirationDateFieldName(fieldName: String?): VGSBlinkCardIntentBuilder = apply {
        this.expDateFieldName = fieldName
    }

    /**
     * Sets cvc field name. The name is used for data mapping after scan.
     *
     * @param fieldName the name of the cvc field
     */
    fun setCVCFieldName(fieldName: String?): VGSBlinkCardIntentBuilder = apply {
        this.cvcFieldName = fieldName
    }

    /**
     * Sets card holder field name. The name is used for data mapping after scan.
     *
     * @param fieldName the name of the card holder field
     */
    fun setCardHolderFieldName(fieldName: String?): VGSBlinkCardIntentBuilder = apply {
        this.cHolderFieldName = fieldName
    }

    /**
     * Controlling the visibility of the introduction dialog.
     *
     * @param showIntroductionDialog true if introduction dialog should be shown, false otherwise.
     */
    fun setShowIntroductionDialog(showIntroductionDialog: Boolean): VGSBlinkCardIntentBuilder = apply {
        this.showIntroductionDialog = showIntroductionDialog
    }

    /**
     * Controlling the visibility of the onboarding dialog.
     *
     * @param showOnboardingInfoDialog true if onboarding dialog should be shown, false otherwise.
     */
    fun setShowOnboardingInfoDialog(showOnboardingInfoDialog: Boolean): VGSBlinkCardIntentBuilder = apply {
        this.showOnboardingInfoDialog = showOnboardingInfoDialog
    }

    /**
     * Returns ready to use Intent instance which is required to start scanner.
     */
    fun build(): Intent = with(Intent(activity, ScanActivity::class.java)) {
        putExtras(Bundle().apply {
            putString(CARD_NUMBER, ccFieldName)
            putString(CVC, cvcFieldName)
            putString(CARD_HOLDER, cHolderFieldName)
            putString(EXP_DATE, expDateFieldName)
            putBoolean(SHOW_INTRO_DIALOG, showIntroductionDialog)
            putBoolean(SHOW_ONBOARDING_INFO_DIALOG, showOnboardingInfoDialog)
            styleId?.let { putInt(STYLE_RES_ID, it) }
        })
    }
}
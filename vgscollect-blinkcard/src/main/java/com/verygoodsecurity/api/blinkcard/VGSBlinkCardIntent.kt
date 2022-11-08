package com.verygoodsecurity.api.blinkcard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.annotation.StyleRes
import com.verygoodsecurity.api.blinkcard.ScanActivity.Companion.CC
import com.verygoodsecurity.api.blinkcard.ScanActivity.Companion.CVC
import com.verygoodsecurity.api.blinkcard.ScanActivity.Companion.C_HOLDER
import com.verygoodsecurity.api.blinkcard.ScanActivity.Companion.EXP_DATE
import com.verygoodsecurity.api.blinkcard.ScanActivity.Companion.STYLE_RES_ID

class VGSBlinkCardIntent {

    class Builder(private val activity: Activity) {
        private var ccFieldName: String? = null
        private var cvcFieldName: String? = null
        private var expDateFieldName: String? = null
        private var cHolderFieldName: String? = null
        private var styleId: Int? = null

        fun setOverlayViewStyle(@StyleRes resId: Int): Builder = apply {
            styleId = resId
        }

        fun setCardNumberFieldName(fieldName: String?): Builder = apply {
            this.ccFieldName = fieldName
        }

        fun setExpirationDateFieldName(fieldName: String?): Builder = apply {
            this.expDateFieldName = fieldName
        }

        fun setCVCFieldName(fieldName: String?): Builder = apply {
            this.cvcFieldName = fieldName
        }

        fun setCardHolderFieldName(fieldName: String?): Builder = apply {
            this.cHolderFieldName = fieldName
        }

        fun build(): Intent {
            return with(Intent(activity, ScanActivity::class.java)) {
                putExtras(Bundle().apply {
                    putString(CC, ccFieldName)
                    putString(CVC, cvcFieldName)
                    putString(C_HOLDER, cHolderFieldName)
                    putString(EXP_DATE, expDateFieldName)
                    styleId?.let { putInt(STYLE_RES_ID, it) }
                })
            }
        }
    }
}
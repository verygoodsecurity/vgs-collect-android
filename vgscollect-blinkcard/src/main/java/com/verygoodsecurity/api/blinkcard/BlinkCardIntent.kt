package com.verygoodsecurity.api.blinkcard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.microblink.blinkcard.util.RecognizerCompatibility
import com.microblink.blinkcard.util.RecognizerCompatibilityStatus
import com.verygoodsecurity.api.blinkcard.ScanActivity.Companion.CC
import com.verygoodsecurity.api.blinkcard.ScanActivity.Companion.CVC
import com.verygoodsecurity.api.blinkcard.ScanActivity.Companion.C_HOLDER
import com.verygoodsecurity.api.blinkcard.ScanActivity.Companion.EXP_DATE
import com.verygoodsecurity.api.blinkcard.ScanActivity.Companion.KEY
import com.verygoodsecurity.api.blinkcard.ScanActivity.Companion.PATH
import com.verygoodsecurity.api.blinkcard.ScanActivity.Companion.REQUEST_CODE

class BlinkCardIntent {

    class Builder(private val activity: Activity) {
        private var key: String? = null
        private var path: String? = null
        private var ccFieldName: String? = null
        private var cvcFieldName: String? = null
        private var expDateFieldName: String? = null
        private var cHolderFieldName: String? = null
        private var requestCode: Int = 0

        fun setKey(key: String): Builder {
            this.key = key
            return this
        }

        fun setLicenseFile(path: String?): Builder {
            this.path = path
            return this
        }

        fun setCardNumberFieldName(fieldName: String?): Builder {
            this.ccFieldName = fieldName
            return this
        }

        fun setExpirationDateFieldName(fieldName: String?): Builder {
            this.expDateFieldName = fieldName
            return this
        }

        fun setCVCFieldName(fieldName: String?): Builder {
            this.cvcFieldName = fieldName
            return this
        }

        fun setCardHolderFieldName(fieldName: String?): Builder {
            this.cHolderFieldName = fieldName
            return this
        }

        fun setRequestCode(requestCode: Int): Builder {
            this.requestCode = requestCode
            return this
        }

        fun start() {
            val status = RecognizerCompatibility.getRecognizerCompatibilityStatus(activity)
            when {
                status == RecognizerCompatibilityStatus.PROCESSOR_ARCHITECTURE_NOT_SUPPORTED -> Log.e(
                    "BlinkCard",
                    "BlinkCard is not supported on current processor architecture!"
                )
                status == RecognizerCompatibilityStatus.NO_CAMERA -> Log.e(
                    "BlinkCard",
                    "BlinkCard is supported only via Direct API!"
                )
                key.isNullOrEmpty() && path.isNullOrEmpty() -> Log.e("BlinkCard", "licence key is missed")
                status == RecognizerCompatibilityStatus.RECOGNIZER_SUPPORTED -> {
                    with(Intent(activity, ScanActivity::class.java)) {
                        putExtras(Bundle().apply {
                            putString(CC, ccFieldName)
                            putString(CVC, cvcFieldName)
                            putString(C_HOLDER, cHolderFieldName)
                            putString(EXP_DATE, expDateFieldName)
                            putString(KEY, key)
                            putString(PATH, path)
                            putInt(REQUEST_CODE, requestCode)
                        })
                        activity.startActivityForResult(this, requestCode)
                    }
                }
                else -> Log.e("BlinkCard", "BlinkCard is not supported! Reason: ${status.name}")
            }
        }
    }
}
package com.verygoodsecurity.api.blinkcard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.microblink.blinkcard.entities.recognizers.RecognizerBundle
import com.microblink.blinkcard.entities.recognizers.blinkcard.BlinkCardProcessingStatus
import com.microblink.blinkcard.entities.recognizers.blinkcard.BlinkCardRecognizer
import com.microblink.blinkcard.results.date.DateResult
import com.microblink.blinkcard.uisettings.ActivityRunner
import com.microblink.blinkcard.uisettings.BlinkCardUISettings
import com.microblink.blinkcard.util.RecognizerCompatibility
import com.microblink.blinkcard.util.RecognizerCompatibilityStatus
import com.verygoodsecurity.vgscollect.app.BaseTransmitActivity
import java.util.*

internal class ScanActivity : BaseTransmitActivity() {

    private lateinit var mRecognizer: BlinkCardRecognizer
    private lateinit var mRecognizerBundle: RecognizerBundle
    private lateinit var settings: BlinkCardUISettings

    private var requestCode = CODE

    private var styleId: Int? = null
    private var ccFieldName: String? = null
    private var cvcFieldName: String? = null
    private var expDateFieldName: String? = null
    private var cHolderFieldName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parseSettings()

        if (checkCompatibility()) {
            mRecognizer = configureBlinkCardRecognizer()
            mRecognizerBundle = RecognizerBundle(mRecognizer)
            settings = configureBlinkCardUISettings()

            ActivityRunner.startActivityForResult(this, requestCode, settings)
        } else {
            finish()
        }
    }

    private fun checkCompatibility(): Boolean {
        when (RecognizerCompatibility.getRecognizerCompatibilityStatus(this)) {
            RecognizerCompatibilityStatus.NO_CAMERA ->
                notifyFailedStatus(
                    getString(R.string.vgs_bc_warning_direct_api)
                )
            RecognizerCompatibilityStatus.PROCESSOR_ARCHITECTURE_NOT_SUPPORTED ->
                notifyFailedStatus(
                    getString(R.string.vgs_bc_warning_arch_not_supported)
                )
            RecognizerCompatibilityStatus.DEVICE_BLACKLISTED -> notifyFailedStatus(
                getString(
                    R.string.vgs_bc_warning_blacklisted_device,
                    RecognizerCompatibilityStatus.RECOGNIZER_NOT_SUPPORTED.name
                )
            )
            RecognizerCompatibilityStatus.UNSUPPORTED_ANDROID_VERSION -> notifyFailedStatus(
                getString(
                    R.string.vgs_bc_warning_unsupported_android_version,
                    RecognizerCompatibilityStatus.RECOGNIZER_NOT_SUPPORTED.name
                )
            )
            RecognizerCompatibilityStatus.RECOGNIZER_NOT_SUPPORTED -> notifyFailedStatus(
                getString(
                    R.string.vgs_bc_warning_unsupported_recognizer,
                    RecognizerCompatibilityStatus.RECOGNIZER_NOT_SUPPORTED.name
                )
            )
            RecognizerCompatibilityStatus.RECOGNIZER_SUPPORTED -> return true
        }
        return false
    }

    private fun notifyFailedStatus(message: String) = with(message) {
        Log.e(getString(R.string.module_name), this)

        addAnalyticInfo(Status.FAILED, this)
        setScanResult(Activity.RESULT_OK)
    }

    private fun configureBlinkCardRecognizer() = BlinkCardRecognizer().apply {
        setExtractIban(false)
        setExtractCvv(!cvcFieldName.isNullOrEmpty())
        setExtractExpiryDate(!expDateFieldName.isNullOrEmpty())
        setExtractOwner(!cHolderFieldName.isNullOrEmpty())
    }

    private fun configureBlinkCardUISettings() = BlinkCardUISettings(mRecognizerBundle).apply {
        setOverlayViewStyle(styleId ?: return@apply)
    }

    private fun parseSettings() {
        intent.extras?.let {
            ccFieldName = it.getString(CC, "")
            cvcFieldName = it.getString(CVC, "")
            expDateFieldName = it.getString(EXP_DATE, "")
            cHolderFieldName = it.getString(C_HOLDER, "")
            styleId = it.getInt(STYLE_RES_ID)
            requestCode = it.getInt(REQUEST_CODE, CODE)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        checkBlinkResults(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        finish()
    }

    private fun checkBlinkResults(requestCode: Int, resultCode: Int, data: Intent?) {
        if (this.requestCode == requestCode) {
            if (resultCode == RESULT_OK && data != null) {
                processRecognitionResults(data)
            } else {
                addAnalyticInfo(Status.CLOSE)
            }
        }
    }

    private fun processRecognitionResults(data: Intent) {
        mRecognizerBundle.loadFromIntent(data)
        mRecognizer.result.let {
            mapData(ccFieldName, it.cardNumber)
            mapData(cvcFieldName, it.cvv)
            mapData(cHolderFieldName, it.owner)
            mapData(expDateFieldName, parseDate(it.expiryDate))

            when (it.processingStatus) {
                BlinkCardProcessingStatus.Success -> addAnalyticInfo(Status.SUCCESS)
                else -> addAnalyticInfo(Status.FAILED)
            }
        }
    }

    private fun parseDate(originalDate: DateResult): Any {
        return originalDate.date.takeIf { it != null && it.month > 0 && it.year > 0 }
            ?.run {
                Calendar.getInstance().run {
                    set(year, month, day)
                    time.time
                }
            } ?: originalDate.originalDateString
    }

    private fun addAnalyticInfo(status: Status, details: String? = null) {
        mapData(RESULT_TYPE, SCAN)
        mapData(RESULT_NAME, NAME)
        mapData(RESULT_STATUS, status.raw)
        mapData(RESULT_DETAILS, details)
    }

    companion object {

        internal const val CC: String = "VgsCc"
        internal const val CVC: String = "VgsCvc"
        internal const val C_HOLDER: String = "VgsOwner"
        internal const val EXP_DATE: String = "VgsExpDate"
        internal const val STYLE_RES_ID: String = "StyleResId"
        internal const val REQUEST_CODE: String = "RequestCode"
        private const val CODE: Int = 0
        private const val NAME: String = "BlinkCard"
    }
}
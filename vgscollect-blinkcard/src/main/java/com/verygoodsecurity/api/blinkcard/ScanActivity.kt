package com.verygoodsecurity.api.blinkcard

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import com.microblink.blinkcard.activity.result.ResultStatus
import com.microblink.blinkcard.activity.result.contract.MbScan
import com.microblink.blinkcard.entities.recognizers.RecognizerBundle
import com.microblink.blinkcard.entities.recognizers.blinkcard.BlinkCardProcessingStatus
import com.microblink.blinkcard.entities.recognizers.blinkcard.BlinkCardRecognizer
import com.microblink.blinkcard.results.date.Date
import com.microblink.blinkcard.uisettings.BlinkCardUISettings
import com.verygoodsecurity.vgscollect.app.BaseTransmitActivity
import java.util.Calendar

internal class ScanActivity : BaseTransmitActivity() {

    private lateinit var mRecognizer: BlinkCardRecognizer
    private lateinit var mRecognizerBundle: RecognizerBundle
    private lateinit var settings: BlinkCardUISettings

    private var styleId: Int? = null
    private var ccFieldName: String? = null
    private var cvcFieldName: String? = null
    private var expDateFieldName: String? = null
    private var cHolderFieldName: String? = null
    private var showIntroductionDialog: Boolean = true
    private var showOnboardingInfoDialog: Boolean = true

    private val blinkCardScanLauncher = registerForActivityResult(MbScan()) { results ->
        results.resultStatus.run {
            when (this) {
                null -> RESULT_OK
                ResultStatus.FINISHED -> {
                    results.result?.let { mRecognizerBundle.loadFromIntent(it) }
                    processRecognitionResults()
                    RESULT_OK
                }

                ResultStatus.CANCELLED -> {
                    addAnalyticInfo(Status.CLOSE)
                    RESULT_CANCELED
                }

                ResultStatus.EXCEPTION -> {
                    notifyFailedStatus(getString(R.string.vgs_bc_warning_exception))
                    RESULT_OK
                }
            }
        }.also {
            setScanResult(it)
        }
        finish()
    }

    private fun processRecognitionResults() {
        mRecognizer.result.let {
            mapData(ccFieldName, it.cardNumber)
            mapData(cvcFieldName, it.cvv)
            mapData(cHolderFieldName, it.owner)
            mapData(expDateFieldName, it.expiryDate.parseDate())

            if (it.processingStatus == BlinkCardProcessingStatus.Success) {
                addAnalyticInfo(Status.SUCCESS)
            } else {
                notifyFailedStatus(
                    getString(R.string.vgs_bc_warning_exception_details, it.processingStatus)
                )
            }
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        parseSettings()

        if (isScannerCompatible(::notifyFailedStatus)) {
            mRecognizer = configureBlinkCardRecognizer()
            mRecognizerBundle = RecognizerBundle(mRecognizer)
            settings = configureBlinkCardUISettings()

            blinkCardScanLauncher.launch(settings)
        } else {
            setScanResult(RESULT_OK)
            finish()
        }
    }

    private fun parseSettings() {
        intent.extras?.let {
            styleId = it.getInt(STYLE_RES_ID)
            ccFieldName = it.getString(CARD_NUMBER, "")
            cvcFieldName = it.getString(CVC, "")
            expDateFieldName = it.getString(EXP_DATE, "")
            cHolderFieldName = it.getString(CARD_HOLDER, "")
            showIntroductionDialog = it.getBoolean(SHOW_INTRO_DIALOG, showIntroductionDialog)
            showOnboardingInfoDialog =
                it.getBoolean(SHOW_ONBOARDING_INFO_DIALOG, showOnboardingInfoDialog)
        }
    }

    private fun notifyFailedStatus(message: String) = with(message) {
        Log.w(getString(R.string.module_name), this)
        addAnalyticInfo(Status.FAILED, this)
    }

    private fun configureBlinkCardRecognizer() = BlinkCardRecognizer().apply {
        setExtractIban(false)
        setExtractCvv(!cvcFieldName.isNullOrEmpty())
        setExtractExpiryDate(!expDateFieldName.isNullOrEmpty())
        setExtractOwner(!cHolderFieldName.isNullOrEmpty())
    }

    private fun configureBlinkCardUISettings() = BlinkCardUISettings(mRecognizerBundle).apply {
        setOverlayViewStyle(styleId ?: return@apply)
        setShowIntroductionDialog(showIntroductionDialog)
        setShowOnboardingInfo(showOnboardingInfoDialog)
    }


    private fun addAnalyticInfo(status: Status, details: String? = null) {
        mapData(RESULT_TYPE, SCAN)
        mapData(RESULT_NAME, NAME)
        mapData(RESULT_STATUS, status)
        mapData(RESULT_DETAILS, details)
    }

    companion object {

        internal const val STYLE_RES_ID: String = "com.verygoodsecurity.api.blinkcard.style_res_id"
        internal const val CARD_NUMBER: String = "com.verygoodsecurity.api.blinkcard.card_number"
        internal const val CVC: String = "com.verygoodsecurity.api.blinkcard.cvc"
        internal const val CARD_HOLDER: String = "com.verygoodsecurity.api.blinkcard.card_holder"
        internal const val EXP_DATE: String = "com.verygoodsecurity.api.blinkcard.exp_date"
        internal const val SHOW_INTRO_DIALOG: String =
            "com.verygoodsecurity.api.blinkcard.show_intro_dialog"
        internal const val SHOW_ONBOARDING_INFO_DIALOG: String =
            "com.verygoodsecurity.api.blinkcard.show_onboarding_info_dialog"
        private const val NAME: String = "BlinkCard"

        internal fun Date.parseDate(): Any {

            return date.takeIf { it != null && it.month > 0 && it.year > 0 }
                ?.run {
                    Calendar.getInstance().run {
                        set(year, month, day)
                        time.time
                    }
                } ?: originalDateString
        }
    }
}
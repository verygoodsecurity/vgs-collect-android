package com.verygoodsecurity.api.blinkcard

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import com.microblink.blinkcard.activity.result.ScanResult
import com.microblink.blinkcard.activity.result.contract.MbScan
import com.microblink.blinkcard.entities.recognizers.RecognizerBundle
import com.microblink.blinkcard.entities.recognizers.blinkcard.BlinkCardProcessingStatus
import com.microblink.blinkcard.entities.recognizers.blinkcard.BlinkCardRecognizer
import com.microblink.blinkcard.results.date.DateResult
import com.microblink.blinkcard.uisettings.BlinkCardUISettings
import com.verygoodsecurity.vgscollect.app.BaseTransmitActivity
import java.util.*

internal class ScanActivity : BaseTransmitActivity() {

    private lateinit var mRecognizer: BlinkCardRecognizer
    private lateinit var mRecognizerBundle: RecognizerBundle
    private lateinit var settings: BlinkCardUISettings

    private var styleId: Int? = null
    private var ccFieldName: String? = null
    private var cvcFieldName: String? = null
    private var expDateFieldName: String? = null
    private var cHolderFieldName: String? = null

    private val blinkCardScanLauncher = registerForActivityResult(MbScan()) {
        checkBlinkResults(it)
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
            finish()
        }
    }

    private fun checkBlinkResults(result: ScanResult) {
        when (result.resultStatus) {
            ScanResult.ResultStatus.FINISHED -> parseScanResult(
                RESULT_OK,
                result.data
            )
            ScanResult.ResultStatus.CANCELLED -> parseScanResult(
                RESULT_CANCELED
            )
            else -> parseScanResult(RESULT_OK)
        }
    }

    private fun parseScanResult(resultCode: Int, data: Intent? = null) {
        when (resultCode) {
            RESULT_OK -> data?.let {
                processRecognitionResults(data)
            } ?: notifyFailedStatus(getString(R.string.vgs_bc_warning_exception))
            RESULT_CANCELED -> addAnalyticInfo(Status.CLOSE)
        }
        setScanResult(resultCode)
        finish()
    }

    private fun notifyFailedStatus(message: String) = with(message) {
        Log.e(getString(R.string.module_name), this)
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
    }

    private fun parseSettings() {
        intent.extras?.let {
            ccFieldName = it.getString(CC, "")
            cvcFieldName = it.getString(CVC, "")
            expDateFieldName = it.getString(EXP_DATE, "")
            cHolderFieldName = it.getString(C_HOLDER, "")
            styleId = it.getInt(STYLE_RES_ID)
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
        private const val NAME: String = "BlinkCard"
    }
}
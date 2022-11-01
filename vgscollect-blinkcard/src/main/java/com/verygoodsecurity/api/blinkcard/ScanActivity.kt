package com.verygoodsecurity.api.blinkcard

import android.content.Intent
import android.os.Bundle
import com.microblink.blinkcard.MicroblinkSDK
import com.microblink.blinkcard.entities.recognizers.RecognizerBundle
import com.microblink.blinkcard.entities.recognizers.blinkcard.BlinkCardProcessingStatus
import com.microblink.blinkcard.entities.recognizers.blinkcard.BlinkCardRecognizer
import com.microblink.blinkcard.results.date.DateResult
import com.microblink.blinkcard.uisettings.ActivityRunner
import com.microblink.blinkcard.uisettings.BlinkCardUISettings
import com.verygoodsecurity.vgscollect.app.BaseTransmitActivity
import java.text.SimpleDateFormat
import java.util.*


class ScanActivity : BaseTransmitActivity() {

    private lateinit var mRecognizer: BlinkCardRecognizer
    private lateinit var mRecognizerBundle: RecognizerBundle
    private lateinit var settings: BlinkCardUISettings

    private var requestCode = CODE

    private var key: String? = null
    private var path: String? = null
    private var styleId: Int? = null
    private var ccFieldName: String? = null
    private var cvcFieldName: String? = null
    private var expDateFieldName: String? = null
    private var cHolderFieldName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parseSettings()
        configureKey()

        mRecognizer = configureBlinkCardRecognizer()
        mRecognizerBundle = RecognizerBundle(mRecognizer)
        settings = configureBlinkCardUISettings()

        scanCard()
    }

    private fun configureBlinkCardRecognizer() = BlinkCardRecognizer().apply {
        setExtractIban(false)
        setExtractCvv(!cvcFieldName.isNullOrEmpty())
        setExtractExpiryDate(!expDateFieldName.isNullOrEmpty())
        setExtractOwner(!cHolderFieldName.isNullOrEmpty())
    }

    private fun configureBlinkCardUISettings() = BlinkCardUISettings(mRecognizerBundle).apply {
        if (styleId != null) setOverlayViewStyle(styleId!!)
    }

    private fun parseSettings() {
        intent.extras?.let {
            key = it.getString(KEY, "")
            path = it.getString(PATH, "")
            ccFieldName = it.getString(CC, "")
            cvcFieldName = it.getString(CVC, "")
            expDateFieldName = it.getString(EXP_DATE, "")
            cHolderFieldName = it.getString(C_HOLDER, "")
            styleId = it.getInt(STYLE_RES_ID)
            requestCode = it.getInt(REQUEST_CODE, CODE)
        }
    }

    private fun configureKey() {
        when {
            !key.isNullOrEmpty() -> MicroblinkSDK.setLicenseKey(key!!, this)
            !path.isNullOrEmpty() -> MicroblinkSDK.setLicenseKey(path!!, this)
            else -> finish()
        }
    }

    private fun scanCard() {
        ActivityRunner.startActivityForResult(this, requestCode, settings)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        checkBlinkResults(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        finish()
    }

    private fun checkBlinkResults(requestCode: Int, resultCode: Int, data: Intent?) {
        if (this.requestCode == requestCode) {
            if (resultCode == RESULT_OK && data != null) {
                processRecognitionRelusts(data)
            } else {
                addAnalyticInfo(Status.CLOSE)
            }
        }
    }

    private fun processRecognitionRelusts(data: Intent) {
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

    private fun addAnalyticInfo(status: Status) {
        mapData(RESULT_TYPE, SCAN)
        mapData(RESULT_NAME, NAME)
        mapData(RESULT_STATUS, status.raw)
    }

    companion object {

        internal const val CC = "vgs-cc"
        internal const val CVC = "vgs-cvc"
        internal const val C_HOLDER = "vgsc-c-owner"
        internal const val EXP_DATE = "vgs-exp-date"
        internal const val KEY = "blink-key"
        internal const val PATH = "blink-path"
        internal const val STYLE_RES_ID = "style-res-id"
        internal const val REQUEST_CODE = "request-code"
        private const val CODE = 0
        private const val NAME = "BlinkCard"
    }
}
package com.verygoodsecurity.api.bouncer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.getbouncer.cardscan.ui.CardScanActivity
import com.getbouncer.cardscan.ui.CardScanActivityResult
import com.getbouncer.cardscan.ui.CardScanActivityResultHandler
import com.getbouncer.scan.framework.exception.InvalidBouncerApiKeyException
import com.verygoodsecurity.vgscollect.BuildConfig
import com.verygoodsecurity.vgscollect.app.BaseTransmitActivity
import java.text.SimpleDateFormat
import java.util.*

class ScanActivity : BaseTransmitActivity(), CardScanActivityResultHandler {

    private lateinit var key: String

    private lateinit var settings: Map<String, Int>

    private var enableEnterCardManually = false
    private var enableExpiryExtraction = false
    private var enableNameExtraction = false
    private var displayCardPan = false
    private var displayCardholderName = false
    private var displayCardScanLogo = false
    private var enableDebug = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        saveSettings()

        try {
            val initializeNameAndExpiryExtraction = enableNameExtraction || enableExpiryExtraction
            CardScanActivity.warmUp(this, key, initializeNameAndExpiryExtraction)
        } catch (e: InvalidBouncerApiKeyException) {
            printLog(e)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        runCardIO()
    }

    private fun saveSettings() {
        intent.extras?.let {
            settings = it.getSerializable(SCAN_CONFIGURATION)?.run {
                this as HashMap<String, Int>
            } ?: HashMap()
            key = it.getString(API_KEY, "")

            enableExpiryExtraction = it.getBoolean(ENABLE_EXPIRY_EXTRACTION)
            enableNameExtraction = it.getBoolean(ENABLE_NAME_EXTRACTION)
            displayCardPan = it.getBoolean(DISPLAY_CARD_PAN)
            displayCardholderName = it.getBoolean(DISPLAY_CARD_HOLDER_NAME)
            displayCardScanLogo = it.getBoolean(DISPLAY_CARD_SCAN_LOGO)
            enableDebug = it.getBoolean(ENABLE_DEBUG)
        }
    }

    private fun runCardIO() {
        CardScanActivity.start(
            this,
            key,
            enableEnterCardManually,
            enableExpiryExtraction,
            enableNameExtraction
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        CardScanActivity.parseScanResult(resultCode, data, this)
        finish()
    }

    override fun analyzerFailure(scanId: String?) {}

    override fun cameraError(scanId: String?) {
        addAnalyticInfo(scanId, Status.FAILED)
        setResult(Activity.RESULT_OK, resultIntent)
    }

    override fun canceledUnknown(scanId: String?) {
        addAnalyticInfo(scanId, Status.FAILED)
        setResult(Activity.RESULT_OK, resultIntent)
    }

    override fun enterManually(scanId: String?) {}

    override fun userCanceled(scanId: String?) {
        addAnalyticInfo(scanId, Status.CLOSE)
        setResult(Activity.RESULT_OK, resultIntent)
    }

    override fun cardScanned(scanId: String?, scanResult: CardScanActivityResult) {
        settings.forEach {
            when (it.value) {
                CARD_NUMBER -> mapData(it.key, scanResult.pan)
                CARD_CVC -> mapData(it.key, scanResult.cvc)
                CARD_HOLDER -> mapData(it.key, scanResult.cardholderName)
                CARD_EXP_DATE -> mapData(it.key, retrieveDate(scanResult))
            }
        }

        addAnalyticInfo(scanId, Status.SUCCESS)
    }

    private fun retrieveDate(scanResult: CardScanActivityResult): Long? {
        val day: Int
        val mounts: Int
        val year: Int

        scanResult.also {
            day = it.expiryDay?.toIntOrNull() ?: 0
        }.also {
            mounts = it.expiryMonth?.toIntOrNull() ?: 0
        }.also {
            year = it.expiryYear?.toIntOrNull() ?: 0
        }

        return if (mounts != 0 && year != 0) {
            val dStr: String
            val dMask: String
            if (day != 0) {
                dStr = String.format("%02d", day) + "/"
                dMask = day.toString().replace("\\d".toRegex(), "d") + "/"
            } else {
                dStr = ""
                dMask = ""
            }

            val mMask = String.format("%02d", mounts)
                .replace("\\d".toRegex(), "M") + "/"

            val yMask = mounts.toString()
                .replace("\\d".toRegex(), "y")

            val mStr = String.format("%02d", mounts) + "/"
            val yStr = year.toString()
            val date = SimpleDateFormat("$dMask$mMask$yMask", Locale.US)
                .parse("$dStr$mStr$yStr")
            date?.time
        } else {
            null
        }
    }

    private fun addAnalyticInfo(scanId: String?, status: Status) {
        mapData(RESULT_TYPE, SCAN)
        mapData(RESULT_NAME, NAME)
        mapData(RESULT_ID, scanId)
        mapData(RESULT_STATUS, status.raw)
    }

    companion object {
        private const val NAME = "Bouncer"
        const val SCAN_CONFIGURATION = "vgs_scan_settings"

        const val CARD_NUMBER = 0x71
        const val CARD_CVC = 0x72
        const val CARD_HOLDER = 0x73
        const val CARD_EXP_DATE = 0x74

        /**
        The bouncer API key used to run scanning.
         */
        const val API_KEY = "apikey"

        /**
        If true, attempt to extract the card expiry.
         */
        const val ENABLE_EXPIRY_EXTRACTION = "enableExpiryExtraction"

        /**
        If true, attempt to extract the cardholder name.
         */
        const val ENABLE_NAME_EXTRACTION = "enableNameExtraction"

        /**
        If true, display the card pan once the card has started to scan.
         */
        private const val DISPLAY_CARD_PAN = "displayCardPan"

        /**
        If true, display the name of the card owner if extracted.
         */
        private const val DISPLAY_CARD_HOLDER_NAME = "displayCardholderName"

        /**
        If true, display the cardscan.io logo at the top of the screen.
         */
        private const val DISPLAY_CARD_SCAN_LOGO = "displayCardScanLogo"

        /**
        If true, enable debug views in card scan.
         */
        private const val ENABLE_DEBUG = "enableDebug"

        /**
        Start the card scanner activity.
         */
        fun scan(context: Activity, code: Int, bndl: Bundle = Bundle.EMPTY) {
            val intent = Intent(context, ScanActivity::class.java)
            intent.putExtras(bndl)
            context.startActivityForResult(intent, code)
        }
    }

    private fun printLog(e: Exception) {
        if (BuildConfig.DEBUG) {
            Log.e(NAME, e.message ?: "", e)
        }
    }
}
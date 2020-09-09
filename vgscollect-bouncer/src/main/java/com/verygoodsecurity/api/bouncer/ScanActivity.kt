package com.verygoodsecurity.api.bouncer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.getbouncer.cardscan.ui.CardScanActivity
import com.getbouncer.cardscan.ui.CardScanActivityResult
import com.getbouncer.cardscan.ui.CardScanActivityResultHandler
import com.verygoodsecurity.vgscollect.app.BaseTransmitActivity
import java.text.SimpleDateFormat
import java.util.*

class ScanActivity: BaseTransmitActivity(), CardScanActivityResultHandler {

    private lateinit var key:String

    private lateinit var settings:Map<String, Int>

    private var enableEnterCardManually = false
    private var enableExpiryExtraction = false
    private var enableNameExtraction = false
    private var displayCardPan = false
    private var displayCardholderName = false
    private  var displayCardScanLogo = false
    private  var enableDebug = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        saveSettings()

        CardScanActivity.warmUp(this, key, true)
    }

    override fun onStart() {
        super.onStart()
        runCardIO()
    }

    private fun saveSettings() {
        intent.extras?.let {
            settings = it.getSerializable(SCAN_CONFIGURATION)?.run {
                this as HashMap<String, Int>
            }?:HashMap()
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
        CardScanActivity.start(this,
            key,
            enableEnterCardManually,
            enableExpiryExtraction,
            enableNameExtraction,
            displayCardPan,
            displayCardholderName,
            displayCardScanLogo,
            enableDebug
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        CardScanActivity.parseScanResult(resultCode, data, this)
        super.onActivityResult(requestCode, resultCode, data)
        finish()
    }

    override fun analyzerFailure(scanId: String?) {
        configureInternalSettings(scanId, Status.FAILED.raw)
    }

    override fun cameraError(scanId: String?) {
        configureInternalSettings(scanId, Status.FAILED.raw)
    }

    override fun canceledUnknown(scanId: String?) {
        configureInternalSettings(scanId, Status.FAILED.raw)
    }

    override fun enterManually(scanId: String?) {}

    override fun userCanceled(scanId: String?) {
        configureInternalSettings(scanId, Status.CLOSE.raw)
    }

    override fun cardScanned(scanId: String?, scanResult: CardScanActivityResult) {
        configureInternalSettings(scanId, Status.SUCCESS.raw)
        settings.forEach {
            when(it.value) {
                CARD_NUMBER -> mapData(it.key, scanResult.pan)
                CARD_CVC -> mapData(it.key, scanResult.cvc)
                CARD_HOLDER -> mapData(it.key, scanResult.cardholderName)
                CARD_EXP_DATE -> mapData(it.key, retrieveDate(scanResult))
            }
        }
    }

    private fun configureInternalSettings(scanId:String?, status:String) {
        mapData(RESULT_TYPE, SCAN)
        mapData(RESULT_NAME, NAME)
        mapData(RESULT_STATUS, status)
        mapData(RESULT_ID, scanId)
    }

    private fun retrieveDate(scanResult: CardScanActivityResult): Long? {
        val day:Int
        val mounts:Int
        val year:Int

        scanResult.also {
            day = it.expiryDay?.toIntOrNull()?:0
        }.also {
            mounts = it.expiryMonth?.toIntOrNull()?:0
        }.also {
            year = it.expiryYear?.toIntOrNull()?:0
        }

        return if(mounts != 0 && year != 0) {
            val dStr:String
            val dMask:String
            if(day != 0) {
                dStr = String.format("%02d", day)+"/"
                dMask = day.toString().replace("\\d".toRegex(), "d")+"/"
            } else {
                dStr = ""
                dMask = ""
            }


            val mMask = String.format("%02d", mounts)
                .replace("\\d".toRegex(), "M")+"/"

            val yMask = mounts.toString()
                .replace("\\d".toRegex(), "y")

            val mStr = String.format("%02d", mounts)+"/"
            val yStr = year.toString()
            val date = SimpleDateFormat("$dMask$mMask$yMask", Locale.getDefault())
                .parse("$dStr$mStr$yStr")
            date?.time
        } else {
            null
        }
    }

    companion object {
        const val SCAN_CONFIGURATION = "vgs_scan_settings"
        private const val NAME = "Bouncer"

        const val CARD_NUMBER = 0x71
        const val CARD_CVC = 0x72
        const val CARD_HOLDER = 0x73
        const val CARD_EXP_DATE = 0x74

        const val API_KEY = "apikey"
        const val ENABLE_EXPIRY_EXTRACTION = "enableExpiryExtraction"
        const val ENABLE_NAME_EXTRACTION = "enableNameExtraction"
        const val DISPLAY_CARD_PAN = "displayCardPan"
        const val DISPLAY_CARD_HOLDER_NAME = "displayCardholderName"
        const val DISPLAY_CARD_SCAN_LOGO = "displayCardScanLogo"
        const val ENABLE_DEBUG = "enableDebug"

        fun scan(context:Activity, code:Int, bndl:Bundle = Bundle.EMPTY) {
            val intent = Intent(context, ScanActivity::class.java)
            intent.putExtras(bndl)
            context.startActivityForResult(intent, code)
        }
    }

}
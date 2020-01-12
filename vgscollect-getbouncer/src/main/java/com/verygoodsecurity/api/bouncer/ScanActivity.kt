package com.verygoodsecurity.api.bouncer

import android.content.Intent
import android.os.Bundle
import com.getbouncer.cardscan.base.ScanActivityImpl
import com.getbouncer.cardscan.base.ScanBaseActivity
import com.verygoodsecurity.vgscollect.app.BaseTransmitActivity

class ScanActivity: BaseTransmitActivity() {

    companion object {
        const val SCAN_CONFIGURATION = "vgs_scan_settings"

        const val SCAN_CARD_TEXT = ScanActivityImpl.SCAN_CARD_TEXT
        const val POSITION_CARD_TEXT = ScanActivityImpl.POSITION_CARD_TEXT

        const val CARD_NUMBER = 0x71
        const val CARD_EXP_DATE = 0x72

        private const val GETBOUNCER_REQUEST_CODE = 0x71
    }

    private lateinit var settings:Map<String, Int>

    private var scanCardText:String? = null
    private var positionCardText:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        saveSettings()

        runBouncer()
    }

    private fun saveSettings() {
        scanCardText = intent.extras?.getString(SCAN_CARD_TEXT)
        positionCardText = intent.extras?.getString(POSITION_CARD_TEXT)
        settings = intent.extras?.getSerializable(SCAN_CONFIGURATION)?.run {
            this as HashMap<String, Int>
        }?:HashMap()
    }

    private fun runBouncer() {
        ScanBaseActivity.warmUp(this)
        val intent = Intent(this, ScanActivityImpl::class.java)
//            .putExtra(ScanActivityImpl.API_KEY, apiKey)
//            .putExtra(ScanActivityImpl.DELAY_SHOWING_EXPIRATION, apiKey)
            .putExtra(ScanActivityImpl.SCAN_CARD_TEXT, scanCardText)
            .putExtra(ScanActivityImpl.POSITION_CARD_TEXT, positionCardText)

        startActivityForResult(intent, GETBOUNCER_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == GETBOUNCER_REQUEST_CODE) {
            val bndl = data?.extras
            val number:String? = bndl?.getString(ScanActivityImpl.RESULT_CARD_NUMBER)
            val month:Int? = bndl?.getString(ScanActivityImpl.RESULT_EXPIRY_MONTH)?.toIntOrNull()
            val year:Int? = bndl?.getString(ScanActivityImpl.RESULT_EXPIRY_YEAR)?.toIntOrNull()

            settings.forEach {
                when(it.value) {
                    CARD_NUMBER -> mapData(it.key, number)
                    CARD_EXP_DATE -> if(month != null && year != null) {
                        mapData(it.key, String.format("%02d/%02d", month, year))
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
        finish()
    }
}
package com.verygoodsecurity.api.cardio

import android.content.Intent
import android.os.Bundle
import com.verygoodsecurity.vgscollect.app.BaseTransmitActivity
import io.card.payment.CardIOActivity
import io.card.payment.CreditCard

class ScanActivity: BaseTransmitActivity() {

    companion object {
        const val SCAN_CONFIGURATION = "vgs_scan_settings"

        const val CARD_NUMBER = 0x71
        const val CARD_CVC = 0x72
        const val CARD_HOLDER = 0x73
        const val CARD_EXP_DATE = 0x74
        const val POSTAL_CODE = 0x75
        private const val CARD_IO_REQUEST_CODE = 0x7
    }

    private lateinit var settings:Map<String, Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        saveSettings()

        runCardIO()
    }

    private fun saveSettings() {
        settings = intent.extras?.getSerializable(SCAN_CONFIGURATION)?.run {
            this as HashMap<String, Int>
        }?:HashMap()
    }

    private fun runCardIO() {
        val scanIntent = Intent(this, CardIOActivity::class.java)
            .putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, true)
            .putExtra(CardIOActivity.EXTRA_USE_PAYPAL_ACTIONBAR_ICON, true)
            .putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true)
            .putExtra(CardIOActivity.EXTRA_SUPPRESS_CONFIRMATION, true)
            .putExtra(CardIOActivity.EXTRA_SCAN_EXPIRY, true)
            .putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false)
            .putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false)

        startActivityForResult(scanIntent,
            CARD_IO_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == CARD_IO_REQUEST_CODE) {
            val scanResult: CreditCard? = data?.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT)
            scanResult?.run {
                settings.forEach {
                    when(it.value) {
                        CARD_NUMBER -> mapData(it.key, scanResult.cardNumber)
                        CARD_CVC -> mapData(it.key, scanResult.cvv)
                        CARD_HOLDER -> mapData(it.key, scanResult.cardholderName)
                        CARD_EXP_DATE -> if(scanResult.expiryMonth != 0 && scanResult.expiryYear != 0) {
                            mapData(it.key, String.format("%02d/%02d", scanResult.expiryMonth, scanResult.expiryYear))
                        }
                        POSTAL_CODE -> mapData(it.key, scanResult.postalCode)
                    }
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
        finish()
    }
}
package com.verygoodsecurity.api.cardio

import android.content.Intent
import android.os.Bundle
import com.verygoodsecurity.vgscollect.app.BaseTransmitActivity
import io.card.payment.CardIOActivity
import io.card.payment.CreditCard

class ScanActivity: BaseTransmitActivity() {

    companion object {
        const val SCAN_CONFIGURATION = "vgs_scan_settings"

        /**
         * Integer extra. Optional. Defaults to {@link Color#GREEN}. Changes the color of the guide overlay on the
         * camera.
         */
        const val EXTRA_GUIDE_COLOR = CardIOActivity.EXTRA_GUIDE_COLOR

        /**
         * String extra. Optional. The preferred language for all strings appearing in the user
         * interface. If not set, or if set to null, defaults to the device's current language setting.
         **/
        const val EXTRA_LANGUAGE_OR_LOCALE = CardIOActivity.EXTRA_LANGUAGE_OR_LOCALE

        /**
         * String extra. Optional. Used to display instructions to the user while they are scanning
         * their card.
         */
        const val EXTRA_SCAN_INSTRUCTIONS = CardIOActivity.EXTRA_SCAN_INSTRUCTIONS

        const val CARD_NUMBER = 0x71
        const val CARD_CVC = 0x72
        const val CARD_HOLDER = 0x73
        const val CARD_EXP_DATE = 0x74
        const val POSTAL_CODE = 0x75
        private const val CARD_IO_REQUEST_CODE = 0x7
    }

    private lateinit var settings:Map<String, Int>

    private var configuredScanInstructions:String? = null
    private var configuredLocale:String? = null
    private var configuredColor:Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        saveSettings()

        runCardIO()
    }

    private fun saveSettings() {
        intent.extras?.let {
            settings = it.getSerializable(SCAN_CONFIGURATION)?.run {
                this as HashMap<String, Int>
            }?:HashMap()

            configuredScanInstructions = it.getString(EXTRA_SCAN_INSTRUCTIONS, null)
            configuredLocale = it.getString(EXTRA_LANGUAGE_OR_LOCALE, null)
            configuredColor = if(it.containsKey(EXTRA_GUIDE_COLOR)) {
                it.getInt(EXTRA_GUIDE_COLOR, 0)
            } else {
                null
            }
        }
    }

    private fun runCardIO() {
        val scanIntent = Intent(this, CardIOActivity::class.java)
            .putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, true)
            .putExtra(CardIOActivity.EXTRA_USE_PAYPAL_ACTIONBAR_ICON, true)
            .putExtra(CardIOActivity.EXTRA_SUPPRESS_CONFIRMATION, true)
            .putExtra(CardIOActivity.EXTRA_SCAN_EXPIRY, true)
            .putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false)
            .putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, false)
            .putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, true)

        configuredScanInstructions?.let {
            scanIntent.putExtra(CardIOActivity.EXTRA_SCAN_INSTRUCTIONS, it)
        }
        configuredLocale?.let {
            scanIntent.putExtra(CardIOActivity.EXTRA_LANGUAGE_OR_LOCALE, it)
        }
        configuredColor?.let {
            scanIntent.putExtra(CardIOActivity.EXTRA_GUIDE_COLOR, it)
        }


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
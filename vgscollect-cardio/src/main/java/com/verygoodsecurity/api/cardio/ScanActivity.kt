package com.verygoodsecurity.api.cardio

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.verygoodsecurity.api.cardio.extensions.parcelable
import com.verygoodsecurity.api.cardio.extensions.serializable
import com.verygoodsecurity.vgscollect.app.BaseTransmitActivity
import io.card.payment.CardIOActivity
import io.card.payment.CreditCard
import java.text.SimpleDateFormat
import java.util.Locale

class ScanActivity : BaseTransmitActivity() {

    private var settings: HashMap<*, *>? = null
    private var requirePostalCode: Boolean = false
    private var suppressConfirmation: Boolean = true
    private var suppressManualEnter: Boolean = true
    private var keepApplicationTheme: Boolean = false
    private var configuredScanInstructions: String? = null
    private var configuredLocale: String? = null
    private var configuredColor: Int? = null

    private val scanLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            configureInternalSettings(result.data)
            result.data?.parcelable<CreditCard>(CardIOActivity.EXTRA_SCAN_RESULT)?.let { card ->
                settings?.forEach {
                    val key = it.key as? String
                    when (it.value) {
                        CARD_NUMBER -> mapData(key, card.cardNumber)
                        CARD_CVC -> mapData(key, card.cvv)
                        CARD_HOLDER -> mapData(key, card.cardholderName)
                        CARD_EXP_DATE -> mapData(key, retrieveDate(card))
                        POSTAL_CODE -> mapData(key, card.postalCode)
                    }
                }
            }
            setScanResult(result.resultCode)
            finish()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        saveSettings()
        runCardIO()
    }

    private fun saveSettings() {
        intent.extras?.let {
            settings = it.serializable(SCAN_CONFIGURATION)
            requirePostalCode = it.getBoolean(EXTRA_REQUIRE_POSTAL_CODE, false)
            suppressConfirmation = it.getBoolean(EXTRA_SUPPRESS_CONFIRMATION, true)
            suppressManualEnter = it.getBoolean(EXTRA_SUPPRESS_MANUAL_ENTRY, true)
            keepApplicationTheme = it.getBoolean(EXTRA_KEEP_APPLICATION_THEME, false)
            configuredScanInstructions = it.getString(EXTRA_SCAN_INSTRUCTIONS, null)
            configuredLocale = it.getString(EXTRA_LANGUAGE_OR_LOCALE, null)
            configuredColor = if (it.containsKey(EXTRA_GUIDE_COLOR)) {
                it.getInt(EXTRA_GUIDE_COLOR, 0)
            } else {
                null
            }
        }
    }

    private fun runCardIO() {
        val scanIntent = Intent(this, CardIOActivity::class.java).putExtra(
            CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, true
        ).putExtra(CardIOActivity.EXTRA_USE_PAYPAL_ACTIONBAR_ICON, true)
            .putExtra(CardIOActivity.EXTRA_SCAN_EXPIRY, true)
            .putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false)
            .putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, requirePostalCode)
            .putExtra(CardIOActivity.EXTRA_KEEP_APPLICATION_THEME, keepApplicationTheme)


        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, suppressManualEnter)
        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_CONFIRMATION, suppressConfirmation)

        configuredScanInstructions?.let {
            scanIntent.putExtra(CardIOActivity.EXTRA_SCAN_INSTRUCTIONS, it)
        }
        configuredLocale?.let {
            scanIntent.putExtra(CardIOActivity.EXTRA_LANGUAGE_OR_LOCALE, it)
        }
        configuredColor?.let {
            scanIntent.putExtra(CardIOActivity.EXTRA_GUIDE_COLOR, it)
        }


        scanLauncher.launch(scanIntent)
    }

    private fun configureInternalSettings(data: Intent?) {
        mapData(RESULT_TYPE, SCAN)
        mapData(RESULT_NAME, NAME)
        if (data?.extras?.containsKey(CardIOActivity.EXTRA_SCAN_RESULT) == true) {
            mapData(RESULT_STATUS, Status.SUCCESS.raw)
        } else {
            mapData(RESULT_STATUS, Status.FAILED.raw)
        }
    }

    private fun retrieveDate(scanResult: CreditCard): Long? {
        return if (scanResult.expiryMonth != 0 && scanResult.expiryYear != 0) {
            val yMask = scanResult.expiryYear.toString().replace("\\d".toRegex(), "y")
            val mMask = String.format(
                locale = Locale.getDefault(),
                format = "%02d",
                scanResult.expiryMonth
            ).replace("\\d".toRegex(), "M")

            val mStr = String.format(
                locale = Locale.getDefault(),
                format = "%02d",
                scanResult.expiryMonth
            )
            val yStr = scanResult.expiryYear.toString()
            val date = SimpleDateFormat("$mMask/$yMask", Locale.US).parse("$mStr/$yStr")
            date?.time
        } else {
            null
        }
    }


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
        const val EXTRA_SUPPRESS_MANUAL_ENTRY = CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY
        const val EXTRA_SUPPRESS_CONFIRMATION = CardIOActivity.EXTRA_SUPPRESS_CONFIRMATION
        const val EXTRA_REQUIRE_POSTAL_CODE = CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE

        /**
         * Boolean extra. Optional. If this value is set to <code>true</code>,
         * the theme will be set to the theme of the application.
         */
        const val EXTRA_KEEP_APPLICATION_THEME = CardIOActivity.EXTRA_KEEP_APPLICATION_THEME

        const val CARD_NUMBER = 0x71
        const val CARD_CVC = 0x72
        const val CARD_HOLDER = 0x73
        const val CARD_EXP_DATE = 0x74
        const val POSTAL_CODE = 0x75
        private const val NAME = "CardIO"

        fun scan(context: Activity, code: Int, bndl: Bundle = Bundle.EMPTY) {
            val intent = Intent(context, ScanActivity::class.java)
            intent.putExtras(bndl)
            context.startActivityForResult(intent, code)
        }
    }

}
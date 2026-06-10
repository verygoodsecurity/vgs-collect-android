package com.verygoodsecurity.api.blinkcard

import android.os.Build
import android.os.Bundle
import android.util.Log
import com.microblink.blinkcard.core.result.DateResult
import com.microblink.blinkcard.core.session.BlinkCardScanningResult
import com.microblink.blinkcard.ux.contract.BlinkCardScanActivityResult
import com.microblink.blinkcard.ux.contract.BlinkCardScanActivitySettings
import com.microblink.blinkcard.ux.contract.MbBlinkCardScan
import com.microblink.blinkcard.ux.contract.ScanActivityResultStatus
import com.verygoodsecurity.vgscollect.app.BaseTransmitActivity
import java.util.Calendar

internal const val SCAN_ACTIVITY_SETTINGS: String = "com.verygoodsecurity.api.blinkcard.settings"
internal const val CARD_NUMBER: String = "com.verygoodsecurity.api.blinkcard.card_number"
internal const val CVC: String = "com.verygoodsecurity.api.blinkcard.cvc"
internal const val CARD_HOLDER: String = "com.verygoodsecurity.api.blinkcard.card_holder"
internal const val EXP_DATE: String = "com.verygoodsecurity.api.blinkcard.exp_date"
private const val NAME: String = "BlinkCard"
private const val TAG: String = "VGS BlinkCard module"

internal class ScanActivity : BaseTransmitActivity() {

    private var cardScanActivitySettings: BlinkCardScanActivitySettings? = null
    private var ccFieldName: String? = null
    private var cvcFieldName: String? = null
    private var expDateFieldName: String? = null
    private var cHolderFieldName: String? = null

    private val blinkCardScanLauncher = registerForActivityResult(
        MbBlinkCardScan(),
        ::handleScannerResult
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val extras = intent?.extras
        if (extras == null) {
            handleError(getString(R.string.vgs_blinkcard_error_extras_null))
            return
        }
        parseExtras(extras)
        startScanner()
    }

    private fun parseExtras(extras: Bundle) {
        cardScanActivitySettings = extras.getCardScanActivitySettings()
        ccFieldName = extras.getString(CARD_NUMBER, "")
        cvcFieldName = extras.getString(CVC, "")
        expDateFieldName = extras.getString(EXP_DATE, "")
        cHolderFieldName = extras.getString(CARD_HOLDER, "")
    }

    private fun startScanner() {
        cardScanActivitySettings?.let { blinkCardScanLauncher.launch(it) } ?: run {
            handleError(getString(R.string.vgs_blinkcard_error_settings_null))
        }
    }

    private fun handleScannerResult(result: BlinkCardScanActivityResult) {
        when (result.status) {
            ScanActivityResultStatus.Scanned -> handleScanned(result.result)
            ScanActivityResultStatus.Canceled -> handleCanceled()
            ScanActivityResultStatus.ErrorSdkInit -> handleError(getString(R.string.vgs_blinkcard_error_failed_result))
        }
    }

    private fun handleScanned(scanResult: BlinkCardScanningResult?) {
        val cardAccount = scanResult?.cardAccounts?.firstOrNull()
        if (cardAccount == null) {
            handleError(getString(R.string.vgs_blinkcard_error_reading_result))
            return
        }
        mapData(ccFieldName, cardAccount.cardNumber)
        mapData(cvcFieldName, cardAccount.cvv)
        mapData(cHolderFieldName, scanResult.cardholderName)
        mapData(expDateFieldName, cardAccount.expiryDate?.let { parseExpiryDate(it) })
        setAnalyticsInfo(Status.SUCCESS)
        setScanResult(RESULT_OK)
        finish()
    }

    private fun handleCanceled() {
        setAnalyticsInfo(Status.CLOSE)
        setScanResult(RESULT_CANCELED)
        finish()
    }

    private fun handleError(error: String) {
        Log.w(TAG, error)
        setAnalyticsInfo(Status.FAILED, error)
        setScanResult(RESULT_OK)
        finish()
    }

    private fun parseExpiryDate(dateResult: DateResult<String>): Any {
        val month = dateResult.month
        val year = dateResult.year
        return if (month != null && year != null && month > 0 && year > 0) {
            // Normalize 2-digit years: assume 00-99 are in 2000s (valid for card expiry within 10-year window)
            val fullYear = if (year < 100) 2000 + year else year
            Calendar.getInstance().run {
                set(fullYear, month - 1, 1)
                time.time
            }
        } else {
            dateResult.originalString
        }
    }

    private fun setAnalyticsInfo(status: Status, details: String? = null) {
        mapData(RESULT_TYPE, SCAN)
        mapData(RESULT_NAME, NAME)
        mapData(RESULT_STATUS, status)
        mapData(RESULT_DETAILS, details)
    }

    @Suppress("DEPRECATION")
    private fun Bundle.getCardScanActivitySettings(): BlinkCardScanActivitySettings? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelable(SCAN_ACTIVITY_SETTINGS, BlinkCardScanActivitySettings::class.java)
        } else {
            getParcelable(SCAN_ACTIVITY_SETTINGS)
        }
    }
}
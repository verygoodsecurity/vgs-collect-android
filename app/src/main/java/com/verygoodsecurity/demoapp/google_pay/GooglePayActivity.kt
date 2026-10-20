package com.verygoodsecurity.demoapp.google_pay

import android.graphics.Color as AndroidColor
import android.os.Bundle
import android.util.Log
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.contract.TaskResultContracts
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.google_pay.util.Payments
import com.verygoodsecurity.demoapp.start.StartActivity.Companion.KEY_BUNDLE_ENVIRONMENT
import com.verygoodsecurity.demoapp.start.StartActivity.Companion.KEY_BUNDLE_TENANT_ID
import com.verygoodsecurity.demoapp.ui.DemoCard
import com.verygoodsecurity.demoapp.ui.DemoScreen
import com.verygoodsecurity.demoapp.ui.DemoStatusBar
import com.verygoodsecurity.demoapp.ui.DemoStep
import com.verygoodsecurity.demoapp.ui.DemoTheme
import com.verygoodsecurity.demoapp.utils.getStringExtra
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.network.VGSRequest
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import org.json.JSONException

private const val TAG = "GooglePayActivity"

/** Outcome of the last step, shown in the status bar. */
private data class Status(val message: String, val isSuccess: Boolean)

/**
 * Demonstrates decrypting a Google Pay token with VGS Collect.
 *
 * This screen shows:
 * - How to check that Google Pay is available with [PaymentsClient.isReadyToPay]
 * - How to request a payment token with [PaymentsClient.loadPaymentData]
 * - How to send the token to the vault with [VGSCollect.asyncSubmit] so it is decrypted and saved
 *
 * Layout, styling and the status bar come from the shared demo components in
 * `com.verygoodsecurity.demoapp.ui`, so this file only contains Google Pay and SDK usage.
 */
class GooglePayActivity : AppCompatActivity(), VgsCollectResponseListener {

    private val paymentDataLauncher = registerForActivityResult(
        TaskResultContracts.GetPaymentDataResult()
    ) { taskResult ->
        isRequestingToken = false
        when (taskResult.status.statusCode) {
            CommonStatusCodes.SUCCESS -> handlePaymentSuccess(taskResult.result)
            CommonStatusCodes.CANCELED -> status = Status(getString(R.string.google_pay_status_canceled), false)
            else -> status = Status(
                getString(R.string.google_pay_status_failed, taskResult.status.statusCode),
                false
            )
        }
    }

    private val client: PaymentsClient by lazy { Payments.createPaymentsClient(this) }

    private val collect: VGSCollect by lazy {
        VGSCollect(
            this,
            getStringExtra(KEY_BUNDLE_TENANT_ID),
            getStringExtra(KEY_BUNDLE_ENVIRONMENT)
        ).apply { addOnResponseListeners(this@GooglePayActivity) }
    }

    private var isGooglePayAvailable by mutableStateOf(false)
    private var isRequestingToken by mutableStateOf(false)
    private var status by mutableStateOf<Status?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The top bar is a dark gradient, so the status bar needs light icons.
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT))
        setContent {
            DemoTheme {
                GooglePayScreen(
                    isAvailable = isGooglePayAvailable,
                    isRequestingToken = isRequestingToken,
                    status = status,
                    onBack = ::finish,
                    onPay = ::requestPaymentToken
                )
            }
        }
        possiblyShowGooglePayButton()
    }

    /** Releases [VGSCollect] resources. */
    override fun onDestroy() {
        super.onDestroy()
        collect.onDestroy()
    }

    override fun onResponse(response: VGSResponse?) {
        Log.d(TAG, response.toString())
        status = when (response) {
            is VGSResponse.SuccessResponse -> Status(getString(R.string.google_pay_status_success), true)
            is VGSResponse.ErrorResponse -> Status(getString(R.string.google_pay_status_decrypt_failed), false)
            else -> return
        }
    }

    // ==========================================================
    // STEP 1: Check that Google Pay is available
    // ==========================================================
    private fun possiblyShowGooglePayButton() {
        val request = IsReadyToPayRequest.fromJson(Payments.isReadyToPayRequest())
        client.isReadyToPay(request).addOnCompleteListener {
            try {
                isGooglePayAvailable = it.getResult(ApiException::class.java) == true
            } catch (exception: ApiException) {
                Log.e(TAG, "Google Pay unavailable", exception)
            }
            if (!isGooglePayAvailable) {
                status = Status(getString(R.string.google_pay_status_unavailable), false)
            }
        }
    }

    // ==========================================================
    // STEP 2: Request a payment token
    // ==========================================================
    private fun requestPaymentToken() {
        isRequestingToken = true
        val request = PaymentDataRequest.fromJson(Payments.paymentDataRequestPayload("10"))
        paymentDataLauncher.launch(client.loadPaymentData(request))
    }

    // ==========================================================
    // STEP 3: Send the token to the vault to decrypt and save it
    // ==========================================================
    private fun handlePaymentSuccess(paymentData: PaymentData?) {
        try {
            decryptAndSaveToken(
                mapOf("google_pay_payload" to Payments.parsePaymentDataResponse(paymentData?.toJson()))
            )
        } catch (e: JSONException) {
            status = Status(getString(R.string.google_pay_status_error, e.toString()), false)
        }
    }

    private fun decryptAndSaveToken(data: Map<String, Any>) {
        collect.asyncSubmit(
            VGSRequest.VGSRequestBuilder()
                .setPath("post")
                .setCustomData(data)
                .build()
        )
    }
}

@Composable
private fun GooglePayScreen(
    isAvailable: Boolean,
    isRequestingToken: Boolean,
    status: Status?,
    onBack: () -> Unit,
    onPay: () -> Unit,
) {
    val colors = DemoTheme.colors
    val spacing = DemoTheme.spacing
    DemoScreen(title = stringResource(R.string.start_google_pay_title), onBack = onBack) {
        DemoCard {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.l)) {
                Box(Modifier.fillMaxWidth().height(DemoTheme.sizes.accentStrip).background(colors.iconBrush))
                Column(
                    Modifier.padding(horizontal = spacing.xl),
                    verticalArrangement = Arrangement.spacedBy(spacing.s)
                ) {
                    Text(
                        stringResource(R.string.google_pay_overline).uppercase(),
                        style = DemoTheme.typography.overline,
                        color = colors.section
                    )
                    Text(
                        stringResource(R.string.google_pay_headline),
                        style = DemoTheme.typography.headline,
                        color = colors.ink
                    )
                    Text(
                        stringResource(R.string.google_pay_body),
                        style = DemoTheme.typography.body,
                        color = colors.ink
                    )
                }
                Column(
                    Modifier.padding(start = spacing.xl, end = spacing.xl, bottom = spacing.xl),
                    verticalArrangement = Arrangement.spacedBy(spacing.sm)
                ) {
                    DemoStep(1, stringResource(R.string.google_pay_step_request))
                    DemoStep(2, stringResource(R.string.google_pay_step_send))
                    DemoStep(3, stringResource(R.string.google_pay_step_decrypt))
                }
            }
        }

        GooglePayButton(onClick = onPay, enabled = isAvailable && !isRequestingToken)

        Text(
            stringResource(R.string.google_pay_sandbox_note),
            Modifier.fillMaxWidth(),
            style = DemoTheme.typography.overline,
            color = colors.muted,
            textAlign = TextAlign.Center
        )

        status?.let { DemoStatusBar(it.message, it.isSuccess) }
    }
}

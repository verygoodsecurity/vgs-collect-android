package com.verygoodsecurity.demoapp.google_pay

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.contract.TaskResultContracts
import com.verygoodsecurity.demoapp.start.StartActivity
import com.verygoodsecurity.demoapp.databinding.GooglePayDemoActvityBinding
import com.verygoodsecurity.demoapp.google_pay.util.Payments
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.network.VGSRequest
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import org.json.JSONException

class GooglePayActivity : AppCompatActivity(), VgsCollectResponseListener {

    private lateinit var binding: GooglePayDemoActvityBinding

    private val paymentDataLauncher = registerForActivityResult(
        TaskResultContracts.GetPaymentDataResult()
    ) { taskResult ->
        binding.flGooglePayButton.isClickable = true
        when (taskResult.status.statusCode) {
            CommonStatusCodes.SUCCESS -> handlePaymentSuccess(taskResult.result)
            CommonStatusCodes.CANCELED -> showToast("Payment canceled")
            else -> showToast("Payment request failed. Code: ${taskResult.status.statusCode}")
        }
    }

    private val client: PaymentsClient by lazy { Payments.createPaymentsClient(this) }

    private val collect: VGSCollect by lazy {
        with(intent?.extras) {
            VGSCollect(
                this@GooglePayActivity,
                this?.getString(StartActivity.KEY_BUNDLE_VAULT_ID) ?: "",
                this?.getString(StartActivity.KEY_BUNDLE_ENVIRONMENT) ?: ""
            ).apply { addOnResponseListeners(this@GooglePayActivity) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = GooglePayDemoActvityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, windowInsets ->
            val bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = bars.top)
            WindowInsetsCompat.CONSUMED
        }
        possiblyShowGooglePayButton()
    }

    override fun onResponse(response: VGSResponse?) {
        when (response) {
            is VGSResponse.SuccessResponse -> showToast("Payment data successfully decrypted and saved")
            is VGSResponse.ErrorResponse -> showToast("Payment data decryption failed")
            else -> throw IllegalArgumentException("Not implemented")
        }
        Log.d(this::class.java.simpleName, response.toString())
    }

    private fun possiblyShowGooglePayButton() {
        val request = IsReadyToPayRequest.fromJson(Payments.isReadyToPayRequest())
        client.isReadyToPay(request).addOnCompleteListener {
            try {
                it.getResult(ApiException::class.java)?.let(::setGooglePayAvailable)
            } catch (exception: ApiException) {
                showToast("Google pay unavailable. Reason: $exception")
            }
        }
    }

    private fun setGooglePayAvailable(available: Boolean) {
        if (available) {
            binding.flGooglePayButton.visibility = View.VISIBLE
            binding.flGooglePayButton.setOnClickListener { requestPaymentToken() }
        } else {
            showToast("Google pay unavailable.")
        }
    }

    private fun requestPaymentToken() {
        binding.flGooglePayButton.isClickable = false
        val request = PaymentDataRequest.fromJson(Payments.paymentDataRequestPayload("10"))
        paymentDataLauncher.launch(client.loadPaymentData(request))
    }

    private fun handlePaymentSuccess(paymentData: PaymentData?) {
        try {
            decryptAndSaveToken(
                mapOf("google_pay_payload" to Payments.parsePaymentDataResponse(paymentData?.toJson()))
            )
        } catch (e: JSONException) {
            showToast("Payment request failed. Error: $e")
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

    private fun showToast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, msg, duration).show()
    }
}
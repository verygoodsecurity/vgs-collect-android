package com.verygoodsecurity.demoapp.google_pay

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.*
import com.verygoodsecurity.demoapp.databinding.GooglePayDemoActvityBinding
import com.verygoodsecurity.vgscollect.core.Environment
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.network.VGSRequest
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class GooglePayActivity : AppCompatActivity(), VgsCollectResponseListener {

    companion object {

        private const val PAYMENT_REQUEST_CODE = 999
    }

    private lateinit var binding: GooglePayDemoActvityBinding

    private val client: PaymentsClient by lazy {
        Wallet.getPaymentsClient(
            this, Wallet.WalletOptions.Builder()
                .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                .build()
        )
    }

    private val collect: VGSCollect by lazy {
        VGSCollect(this, "tnt6mrrzrrp", Environment.SANDBOX).also {
            it.addOnResponseListeners(this)
        }
    }

    private val baseRequest = JSONObject().apply {
        put("apiVersion", 2)
        put("apiVersionMinor", 0)
    }

    private val baseCardPaymentMethod = JSONObject().apply {
        put("type", "CARD")
        put("parameters", JSONObject().apply {
            put("allowedAuthMethods", JSONArray(listOf("PAN_ONLY", "CRYPTOGRAM_3DS")))
            put(
                "allowedCardNetworks",
                JSONArray(listOf("AMEX", "DISCOVER", "INTERAC", "JCB", "MASTERCARD", "VISA"))
            )
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GooglePayDemoActvityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        possiblyShowGooglePayButton()
    }

    @Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PAYMENT_REQUEST_CODE) {
            handlePaymentRequestResult(resultCode, data)
        }
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
        val isReadyToPayJson = isReadyToPayRequest()
        val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString())
        val task = client.isReadyToPay(request)
        task.addOnCompleteListener { completedTask ->
            try {
                completedTask.getResult(ApiException::class.java)?.let(::setGooglePayAvailable)
            } catch (exception: ApiException) {
                // Process error
                Log.w("isReadyToPay failed", exception)
            }
        }
    }

    private fun isReadyToPayRequest(): JSONObject {
        return baseRequest.apply {
            put("allowedPaymentMethods", JSONArray().put(baseCardPaymentMethod))
        }
    }

    private fun setGooglePayAvailable(available: Boolean) {
        if (available) {
            binding.flGooglePayButton.visibility = View.VISIBLE
            binding.flGooglePayButton.setOnClickListener { requestPayment() }
        } else {
            showToast("Google pay unavailable.")
        }
    }

    private fun requestPayment() {
        binding.flGooglePayButton.isClickable = false
        val request = PaymentDataRequest.fromJson(paymentDataRequest().toString())
        AutoResolveHelper.resolveTask(client.loadPaymentData(request), this, PAYMENT_REQUEST_CODE)
    }

    private fun paymentDataRequest(): JSONObject {
        return baseRequest.apply {
            put("merchantInfo", JSONObject().put("merchantName", "Example Merchant"))
            put("allowedPaymentMethods", JSONArray().put(baseCardPaymentMethod.apply {
                put("tokenizationSpecification", JSONObject().apply {
                    put("type", "PAYMENT_GATEWAY")
                    put(
                        "parameters", JSONObject(
                            mapOf(
                                "gateway" to "verygoodsecurity",
                                "gatewayMerchantId" to "ACk4FamfFXgF8vRTqsuEPvvw"
                            )
                        )
                    )
                })
            }))
            put("transactionInfo", JSONObject().apply {
                put("totalPrice", "11.10")
                put("totalPriceStatus", "FINAL")
                put("totalPriceLabel", "Total")
                put("countryCode", "US")
                put("currencyCode", "USD")
                put("displayItems", JSONArray().apply {
                    put(JSONObject().apply {
                        put("label", "Subtotal")
                        put("type", "SUBTOTAL")
                        put("price", "11.00")
                    })
                    put(JSONObject().apply {
                        put("label", "Tax")
                        put("type", "TAX")
                        put("price", "0.10")
                    })
                })
            })
        }
    }

    private fun handlePaymentRequestResult(resultCode: Int, data: Intent?) {
        binding.flGooglePayButton.isClickable = true
        when (resultCode) {
            RESULT_OK -> handlePaymentSuccess(data)
            RESULT_CANCELED -> showToast("Payment canceled")
            AutoResolveHelper.RESULT_ERROR -> AutoResolveHelper.getStatusFromIntent(data)?.let {
                showToast("Payment request failed. Code: ${it.statusCode}")
            }
        }
    }

    private fun handlePaymentSuccess(data: Intent?) {
        try {
            val paymentData = data?.let { PaymentData.getFromIntent(it) }?.toJson()
                ?: throw IllegalStateException("Payment data is null")

            val paymentMethodData = JSONObject(paymentData).getJSONObject("paymentMethodData")
            val tokenizationData = paymentMethodData.getJSONObject("tokenizationData")
            val token = JSONObject(tokenizationData.getString("token"))
            Log.d(this::class.java.simpleName, token.toString(4))
            val intermediateSigningKey = token.getJSONObject("intermediateSigningKey")
            val signaturesJsonArray = intermediateSigningKey.getJSONArray("signatures")

            val signatures = arrayListOf<String>()
            for (i in 0 until signaturesJsonArray.length()) {
                signatures.add(signaturesJsonArray.getString(i))
            }

            decryptAndSaveToken(
                mapOf(
                    "google_pay_payload" to mapOf(
                        "token" to mapOf(
                            "signature" to token.getString("signature"),
                            "intermediateSigningKey" to mapOf(
                                "signedKey" to token.getJSONObject("intermediateSigningKey")
                                    .getString("signedKey"),
                                "signatures" to signatures
                            ),
                            "protocolVersion" to token.getString("protocolVersion"),
                            "signedMessage" to token.getString("signedMessage")
                        )
                    )
                )
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
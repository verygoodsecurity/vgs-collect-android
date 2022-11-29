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

    private lateinit var binding: GooglePayDemoActvityBinding
    private lateinit var paymentsClient: PaymentsClient

    private val baseRequest = JSONObject().apply {
        put("apiVersion", 2)
        put("apiVersionMinor", 0)
    }

    private val allowedCardNetworks = JSONArray(
        listOf(
            "AMEX",
            "DISCOVER",
            "INTERAC",
            "JCB",
            "MASTERCARD",
            "VISA"
        )
    )

    private val allowedCardAuthMethods = JSONArray(
        listOf(
            "PAN_ONLY",
            "CRYPTOGRAM_3DS"
        )
    )

    private val merchantInfo: JSONObject = JSONObject().put("merchantName", "Example Merchant")

    private var collect: VGSCollect? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GooglePayDemoActvityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        paymentsClient = createPaymentsClient()
        possiblyShowGooglePayButton()
        initCollect()
    }

    @Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 999) {
            binding.mbGooglePay.isClickable = true
            when (resultCode) {
                RESULT_OK -> handlePaymentSuccess(data)
                RESULT_CANCELED -> showToast("Payment canceled")
                AutoResolveHelper.RESULT_ERROR -> {}
            }
        }
    }

    override fun onResponse(response: VGSResponse?) {
        Log.d("Test", response.toString())
    }

    private fun handlePaymentSuccess(data: Intent?) {
        try {
            val paymentData = data?.let { PaymentData.getFromIntent(it) }?.toJson()
                ?: throw IllegalStateException("Payment data is null")
            val paymentMethodData = JSONObject(paymentData).getJSONObject("paymentMethodData")
            val token = paymentMethodData.getJSONObject("tokenizationData").getString("token")


            val tokenJson = JSONObject(token)

            val signature = tokenJson.getString("signature")

            val signedKey = tokenJson
                .getJSONObject("intermediateSigningKey")
                .getString("signedKey")

            val signaturesJsonArray =
                tokenJson.getJSONObject("intermediateSigningKey").getJSONArray("signatures")

            val signatures = arrayListOf<String>()
            for (i in 0 until signaturesJsonArray.length()) {
                signatures.add(signaturesJsonArray.getString(i))
            }

            val protocolVersion = tokenJson.getString("protocolVersion")

            val signedMessage = tokenJson.getString("signedMessage")

            val result = JSONObject().apply {
                put("signature", signature)
                put("intermediateSigningKey", JSONObject().apply {
                    put("signedKey", signedKey)
                    put("signatures", signaturesJsonArray)
                })
                put("protocolVersion", protocolVersion)
                put("signedMessage", signedMessage)
            }

            collect?.asyncSubmit(
                VGSRequest.VGSRequestBuilder()
                    .setPath("post")
                    .setCustomData(
                        mapOf(
                            "google_pay_payload" to mapOf(
                                "token" to mapOf(
                                    "signature" to signature,
                                    "intermediateSigningKey" to mapOf(
                                        "signedKey" to signedKey,
                                        "signatures" to signatures
                                    ),
                                    "protocolVersion" to protocolVersion,
                                    "signedMessage" to signedMessage
                                )
                            )
                        ).also {
                            Log.d("Test", it.toString())
                        }
                    )
                    .build()
            )

            Log.d("GooglePaymentToken", result.toString(4))
        } catch (e: JSONException) {
            Log.e("handlePaymentSuccess", "Error: $e")
        }
    }

    private fun createPaymentsClient(): PaymentsClient {
        val walletOptions = Wallet.WalletOptions.Builder()
            .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
            .build()
        return Wallet.getPaymentsClient(this, walletOptions)
    }

    private fun possiblyShowGooglePayButton() {
        val isReadyToPayJson = isReadyToPayRequest() ?: return
        val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString())
        val task = paymentsClient.isReadyToPay(request)
        task.addOnCompleteListener { completedTask ->
            try {
                completedTask.getResult(ApiException::class.java)?.let(::setGooglePayAvailable)
            } catch (exception: ApiException) {
                // Process error
                Log.w("isReadyToPay failed", exception)
            }
        }
    }

    private fun initCollect() {
        with(intent?.extras) {
            collect = VGSCollect(
                this@GooglePayActivity,
                "tnt6mrrzrrp",
                Environment.SANDBOX
            )
            collect?.addOnResponseListeners(this@GooglePayActivity)
        }
    }

    private fun isReadyToPayRequest(): JSONObject? {
        return try {
            baseRequest.apply {
                put("allowedPaymentMethods", JSONArray().put(baseCardPaymentMethod()))
            }

        } catch (e: JSONException) {
            null
        }
    }

    private fun setGooglePayAvailable(available: Boolean) {
        if (available) {
            binding.mbGooglePay.visibility = View.VISIBLE
            binding.mbGooglePay.setOnClickListener { requestPayment() }
        } else {
            showToast("Google pay unavailable")
        }
    }

    private fun requestPayment() {
        binding.mbGooglePay.isClickable = false
        val paymentDataRequest: JSONObject? = getPaymentDataRequest()
        if (paymentDataRequest != null) {
            val request = PaymentDataRequest.fromJson(paymentDataRequest.toString())
            AutoResolveHelper.resolveTask(paymentsClient.loadPaymentData(request), this, 999)
        } else {
            showToast("Can't proceed payment")
        }
    }

    private fun getPaymentDataRequest(): JSONObject? {
        try {
            return baseRequest.apply {
                put("allowedPaymentMethods", JSONArray().put(cardPaymentMethod()))
                put("transactionInfo", getTransactionInfo())
                put("merchantInfo", merchantInfo)
            }
        } catch (e: JSONException) {
            return null
        }
    }

    private fun getTransactionInfo(): JSONObject {
        return JSONObject().apply {
            put("totalPrice", "0.10")
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
        }
    }

    private fun cardPaymentMethod(): JSONObject {
        val cardPaymentMethod = baseCardPaymentMethod()
        cardPaymentMethod.put("tokenizationSpecification", gatewayTokenizationSpecification())
        return cardPaymentMethod
    }

    private fun baseCardPaymentMethod(): JSONObject {
        return JSONObject().apply {
            val parameters = JSONObject().apply {
                put("allowedAuthMethods", allowedCardAuthMethods)
                put("allowedCardNetworks", allowedCardNetworks)
            }
            put("type", "CARD")
            put("parameters", parameters)
        }
    }

    private fun gatewayTokenizationSpecification(): JSONObject {
        return JSONObject().apply {
            put("type", "PAYMENT_GATEWAY")
            put(
                "parameters", JSONObject(
                    mapOf(
                        "gateway" to "verygoodsecurity",
                        "gatewayMerchantId" to "ACk4FamfFXgF8vRTqsuEPvvw"
                    )
                )
            )
        }
    }

    private fun showToast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, msg, duration).show()
    }
}
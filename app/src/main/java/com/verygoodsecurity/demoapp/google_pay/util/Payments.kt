package com.verygoodsecurity.demoapp.google_pay.util

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.google.android.gms.wallet.*
import org.json.JSONArray
import org.json.JSONObject

object Payments {

    private val baseRequest = JSONObject().apply {
        put("apiVersion", 2)
        put("apiVersionMinor", 0)
    }

    fun createPaymentsClient(
        activity: Activity,
        env: Int = WalletConstants.ENVIRONMENT_TEST
    ): PaymentsClient {
        val walletOptions = Wallet.WalletOptions.Builder()
            .setEnvironment(env)
            .build()
        return Wallet.getPaymentsClient(activity, walletOptions)
    }

    fun isReadyToPayRequest(): String {
        return baseRequest.apply {
            put("allowedPaymentMethods", JSONArray().put(baseCardPaymentMethod()))
        }.toString()
    }

    fun paymentDataRequestPayload(price: String): String {
        return baseRequest.apply {
            put("merchantInfo", merchantInfo())
            put("allowedPaymentMethods", JSONArray().put(cardPaymentMethod()))
            put("transactionInfo", transactionInfo(price))
        }.toString()
    }

    @Throws(Exception::class)
    fun parsePaymentDataResponse(data: Intent?): Map<Any, Any> {
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

        return mapOf(
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
    }

    private fun baseCardPaymentMethod(): JSONObject {
        return JSONObject().apply {
            put("type", "CARD")
            put("parameters", JSONObject().apply {
                put("allowedAuthMethods", allowedCardAuthMethods())
                put("allowedCardNetworks", allowedCardNetworks())
            })
        }
    }

    private fun allowedCardAuthMethods(): JSONArray {
        return JSONArray(listOf("PAN_ONLY", "CRYPTOGRAM_3DS"))
    }

    private fun allowedCardNetworks(): JSONArray {
        return JSONArray(listOf("AMEX", "DISCOVER", "INTERAC", "JCB", "MASTERCARD", "VISA"))
    }

    private fun cardPaymentMethod(): JSONObject {
        return baseCardPaymentMethod().apply {
            put("tokenizationSpecification", gatewayTokenizationSpecification())
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

    private fun transactionInfo(price: String): JSONObject {
        return JSONObject().apply {
            put("totalPrice", price)
            put("totalPriceStatus", "FINAL")
            put("totalPriceLabel", "Total")
            put("countryCode", "US")
            put("currencyCode", "USD")
        }
    }

    private fun merchantInfo(): JSONObject {
        return JSONObject().put("merchantName", "Example Merchant")
    }
}
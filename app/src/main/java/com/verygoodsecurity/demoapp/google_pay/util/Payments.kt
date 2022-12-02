package com.verygoodsecurity.demoapp.google_pay.util

import android.app.Activity
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object Payments {

    private const val MERCHANT_NAME = "Example Merchant"

    private val baseRequest = JSONObject().apply {
        put("apiVersion", 2)
        put("apiVersionMinor", 0)
    }

    private val allowedCardNetworks =
        JSONArray(listOf("AMEX", "DISCOVER", "INTERAC", "JCB", "MASTERCARD", "VISA"))

    private val allowedCardAuthMethods = JSONArray(listOf("PAN_ONLY", "CRYPTOGRAM_3DS"))

    private val merchantInfo: JSONObject =
        JSONObject().put("merchantName", "Example Merchant")

    fun createPaymentsClient(
        activity: Activity,
        env: Int = WalletConstants.ENVIRONMENT_TEST
    ): PaymentsClient {
        val walletOptions = Wallet.WalletOptions.Builder()
            .setEnvironment(env)
            .build()
        return Wallet.getPaymentsClient(activity, walletOptions)
    }

    fun isReadyToPayRequest(): JSONObject? {
        return try {
            baseRequest.apply {
                put("allowedPaymentMethods", JSONArray().put(baseCardPaymentMethod()))
            }

        } catch (e: JSONException) {
            null
        }
    }

    fun getPaymentDataRequest(price: String): JSONObject? {
        return try {
            baseRequest.apply {
                put("merchantInfo", getMerchantInfo())
                put("allowedPaymentMethods", cardPaymentMethod())
                put("transactionInfo", getTransactionInfo(price))
            }
        } catch (e: JSONException) {
            null
        }
    }

    private fun cardPaymentMethod(): JSONObject {
        return baseCardPaymentMethod().apply {
            put("tokenizationSpecification", gatewayTokenizationSpecification())
        }
    }

    private fun baseCardPaymentMethod(): JSONObject {
        return JSONObject().apply {
            put("type", "CARD")
            put("parameters", JSONObject().apply {
                put("allowedAuthMethods", allowedCardAuthMethods)
                put("allowedCardNetworks", allowedCardNetworks)
            })
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

    private fun getTransactionInfo(price: String): JSONObject {
        return JSONObject().apply {
            put("totalPrice", price)
            put("totalPriceStatus", "FINAL")
            put("totalPriceLabel", "Total")
            put("countryCode", "US")
            put("currencyCode", "USD")
        }
    }

    private fun getMerchantInfo(): JSONObject {
        return JSONObject().put("merchantName", "Example Merchant")
    }
}
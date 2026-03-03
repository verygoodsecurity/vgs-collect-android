package com.verygoodsecurity.demoapp.payopt.source

import com.verygoodsecurity.demoapp.BuildConfig
import com.verygoodsecurity.demoapp.utils.NetworkHelper
import org.json.JSONArray
import org.json.JSONObject

class RemoteDataSource {

    fun fetchAccessToken(listener: ResponseListener<String>) {
        NetworkHelper.request(BuildConfig.ACCESS_TOKEN_URL, null, emptyList(), {
            try {
                listener.onSuccess(JSONObject(it).getString("access_token"))
            } catch (e: Exception) {
                listener.onError(e.toString())
            }
        }, listener::onError)
    }

    fun createOrder(token: String, listener: ResponseListener<String>) {
        val body = JSONObject().apply {
            put("order", JSONObject().apply {
                put("amount", 50)
                put("currency", "USD")
                put("financial_instrument_types", JSONArray().apply {
                    put("card")
                })
            })
            put("items", JSONArray().apply {
                put(JSONObject().apply {
                    put("name", "Test")
                    put("price", 50)
                    put("quantity", 1)
                })
            })
        }.toString()

        NetworkHelper.request(
            BuildConfig.CREATE_ORDER_URL,
            body,
            listOf("X-Auth-Token" to token, "Content-Type" to "application/json"),
            {
                try {
                    listener.onSuccess(JSONObject(it).getJSONObject("data").getString("id"))
                } catch (e: Exception) {
                    listener.onError(e.toString())
                }
            },
            listener::onError
        )
    }

    fun createPayment(
        token: String, instrumentId: String, orderId: String, listener: ResponseListener<String>
    ) {
        NetworkHelper.request(
            BuildConfig.PAYMENT_URL,
            JSONObject().apply {
                put("order_id", orderId)
                put("source", instrumentId)
            }.toString(),
            listOf("Authorization" to "Bearer $token"),
            listener::onSuccess,
            listener::onError
        )
    }
}
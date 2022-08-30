package com.verygoodsecurity.demoapp.payopt.source

import android.os.Handler
import android.os.Looper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import com.verygoodsecurity.demoapp.BuildConfig
import org.json.JSONArray
import org.json.JSONObject

class RemoteDataSource {

    private val handler = Handler(Looper.getMainLooper())

    fun fetchAccessToken(listener: ResponseListener<String>) {
        Fuel.post(BuildConfig.ACCESS_TOKEN_URL)
            .responseString { _, _, result ->
                handler.post {
                    try {
                        when (result) {
                            is Result.Success -> {
                                val token = JSONObject(result.get()).getString("access_token")
                                listener.onSuccess(token)
                            }
                            is Result.Failure -> listener.onError(result.error.toString())
                        }
                    } catch (e: Exception) {
                        listener.onError(e.toString())
                    }
                }
            }
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

        Fuel.post(BuildConfig.CREATE_ORDER_URL)
            .body(body)
            .header("X-Auth-Token", token)
            .header("Content-Type", "application/json")
            .responseString { _, _, result ->
                handler.post {
                    try {
                        when (result) {
                            is Result.Success -> {
                                val id =
                                    JSONObject(result.get()).getJSONObject("data").getString("id")
                                listener.onSuccess(id)
                            }
                            is Result.Failure -> listener.onError(result.error.toString())
                        }
                    } catch (e: Exception) {
                        listener.onError(e.toString())
                    }
                }
            }
    }

    fun createPayment(
        token: String,
        instrumentId: String,
        orderId: String,
        listener: ResponseListener<String>
    ) {
        val body = JSONObject().apply {
            put("order_id", orderId)
            put("source", instrumentId)
        }.toString()

        Fuel.post(BuildConfig.PAYMENT_URL)
            .body(body)
            .header("Authorization", "Bearer $token")
            .header("Content-Type", "application/json")
            .responseString { _, _, result ->
                handler.post {
                    when (result) {
                        is Result.Success -> listener.onSuccess(result.value)
                        is Result.Failure -> listener.onError(result.error.toString())
                    }
                }
            }
    }
}
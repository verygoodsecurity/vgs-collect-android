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
        makeRequest(BuildConfig.ACCESS_TOKEN_URL, null, emptyList(), {
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

        makeRequest(
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
        makeRequest(
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

    private fun makeRequest(
        url: String,
        body: String?,
        headers: List<Pair<String, Any>>,
        onSuccess: (response: String) -> Unit,
        onError: (message: String) -> Unit
    ) {
        if (url.isEmpty()) {
            onError.invoke("Invalid url!")
            return
        }
        val request = Fuel.post(url)
        body?.let { request.body(it) }
        headers.forEach { request.header(it.first, it.second) }
        request.responseString { _, _, result ->
            handler.post {
                when (result) {
                    is Result.Success -> onSuccess.invoke(result.value)
                    is Result.Failure -> onError.invoke(result.error.toString())
                }
            }
        }
    }
}
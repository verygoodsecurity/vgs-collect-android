package com.verygoodsecurity.demoapp.utils

import android.os.Handler
import android.os.Looper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import com.verygoodsecurity.demoapp.BuildConfig
import org.json.JSONObject

object NetworkHelper {

    private val handler = Handler(Looper.getMainLooper())

    fun request(
        url: String,
        body: List<Pair<String, String>>,
        headers: List<Pair<String, String>>,
        onSuccess: (response: String) -> Unit,
        onError: (message: String) -> Unit
    ) {
        if (url.isEmpty()) {
            onError.invoke("Invalid url!")
            return
        }
        val request = Fuel.post(
            url,
            body
        )
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

    fun request(
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

fun NetworkHelper.accessToken(
    onSuccess: (token: String) -> Unit,
    onError: (message: String) -> Unit
) {
    request(
        url = BuildConfig.ACCESS_TOKEN_URL,
        body = listOf(
            "client_id" to BuildConfig.CLIENT_ID,
            "client_secret" to BuildConfig.CLIENT_SECRET,
            "grant_type" to BuildConfig.GRANT_TYPE
        ),
        headers = listOf("Content-Type" to "application/x-www-form-urlencoded"),
        onSuccess = { response ->
            try {
                val token = JSONObject(response).getString("access_token")
                onSuccess.invoke(token)
            } catch (e: Exception) {
                onError.invoke("Get access token error: ${e.message}")
            }
        },
        onError = {
            onError.invoke("Get access token error: $it")
        }
    )
}
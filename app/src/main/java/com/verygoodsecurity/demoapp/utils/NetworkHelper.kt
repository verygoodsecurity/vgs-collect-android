package com.verygoodsecurity.demoapp.utils

import android.os.Handler
import android.os.Looper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result

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
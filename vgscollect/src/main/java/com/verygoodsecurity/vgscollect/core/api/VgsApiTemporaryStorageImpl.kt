package com.verygoodsecurity.vgscollect.core.api

internal class VgsApiTemporaryStorageImpl : VgsApiTemporaryStorage {
    private val data = HashMap<String, Any>()
    private val headers = HashMap<String, String>()

    override fun setCustomHeaders(headers: Map<String, String>?) {
        headers?.let {
            this@VgsApiTemporaryStorageImpl.headers.putAll(it)
        }
    }

    override fun getCustomHeaders(): HashMap<String, String> = headers

    override fun resetCustomHeaders() {
        headers.clear()
    }

    override fun setCustomData(data: Map<String, Any>?) {
        data?.let {
            this@VgsApiTemporaryStorageImpl.data.putAll(it)
        }
    }

    override fun getCustomData(): HashMap<String, Any> = data

    override fun resetCustomData() {
        data.clear()
    }
}
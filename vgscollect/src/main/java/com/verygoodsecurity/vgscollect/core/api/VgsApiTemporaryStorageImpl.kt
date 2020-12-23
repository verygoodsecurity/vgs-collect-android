package com.verygoodsecurity.vgscollect.core.api

internal class VgsApiTemporaryStorageImpl : VgsApiTemporaryStorage {
    private val data = HashMap<String, Any>()
    private val headers = HashMap<String, String>()

    override fun setCustomHeaders(newheaders: Map<String, String>?) {
        newheaders?.let {
            headers.putAll(it)
        }
    }

    override fun getCustomHeaders(): HashMap<String, String> = headers

    override fun resetCustomHeaders() {
        headers.clear()
    }

    override fun setCustomData(newData: Map<String, Any>?) {
        newData?.let {
            data.putAll(it)
        }
    }

    override fun getCustomData(): HashMap<String, Any> = data

    override fun resetCustomData() {
        data.clear()
    }
}
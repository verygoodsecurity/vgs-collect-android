package com.verygoodsecurity.vgscollect.core.api

class VgsApiTemporaryStorageImpl : VgsApiTemporaryStorage {
    private val data = HashMap<String, String>()
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

    override fun setCustomData(newData: Map<String, String>?) {
        newData?.let {
            data.putAll(it)
        }
    }

    override fun getCustomData(): HashMap<String, String> = data

    override fun resetCustomData() {
        data.clear()
    }
}
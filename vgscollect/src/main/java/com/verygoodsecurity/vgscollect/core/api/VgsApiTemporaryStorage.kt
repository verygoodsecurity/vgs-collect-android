package com.verygoodsecurity.vgscollect.core.api

internal interface VgsApiTemporaryStorage {
    fun setCustomHeaders(headers:Map<String, String>?)
    fun getCustomHeaders():HashMap<String, String>
    fun resetCustomHeaders()

    fun setCustomData(data:Map<String, Any>?)
    fun getCustomData():HashMap<String, Any>
    fun resetCustomData()
}
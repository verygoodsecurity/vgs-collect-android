package com.verygoodsecurity.vgscollect.core.api

interface VgsApiTemporaryStorage {
    fun setCustomHeaders(headers:Map<String, String>?)
    fun getCustomHeaders():Map<String, String>
    fun resetCustomHeaders()

    fun setCustomData(data:Map<String, String>?)
    fun getCustomData():Map<String, String>
    fun resetCustomData()
}
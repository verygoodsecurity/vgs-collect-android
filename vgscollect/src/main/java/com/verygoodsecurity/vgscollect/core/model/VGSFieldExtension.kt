package com.verygoodsecurity.vgscollect.core.model

import android.net.Uri
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import org.json.JSONObject

internal fun MutableCollection<VGSFieldState>.mapUsefulPayloads(): Map<String,String>? {
    val map = mutableMapOf<String,String>()
    for (entry in this) {
        val key = entry.fieldName?:""
        val value = entry.content

        map[key] = value?.data?:""
    }
    return map
}

internal fun Map<String,String>.mapToJson(): String? {
    val jObjectData = JSONObject()
    for (entry in this) {
        jObjectData.put(entry.key, entry.value)
    }
    return jObjectData.toString()
}

internal fun Map<String,String>.mapToEncodedQuery(): String? {
    val builder = Uri.Builder()
    for (entry in this) {
        builder.appendQueryParameter(entry.key, entry.value)
    }
    return builder.build().encodedQuery
}
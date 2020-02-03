package com.verygoodsecurity.vgscollect.core.model

import android.net.Uri
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.view.card.FieldType
import org.json.JSONObject

internal fun MutableCollection<VGSFieldState>.mapUsefulPayloads(
    userData: Map<String, String>? = null
): Map<String,String>? {

    val fieldsState = this.associate {
        val key = it.fieldName?:""
        val value = if(it.type == FieldType.CARD_NUMBER) {
            it.content?.data?.replace("\\s".toRegex(),"")?:""
        } else {
            it.content?.data?:""
        }

        Pair(key, value)
    }.toMutableMap()

    userData?.let { fieldsState.putAll(it) }

    return fieldsState.toMap()
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
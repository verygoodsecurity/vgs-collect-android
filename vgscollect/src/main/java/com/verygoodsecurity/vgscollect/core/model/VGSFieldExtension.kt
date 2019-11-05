package com.verygoodsecurity.vgscollect.core.model

import org.json.JSONObject

fun MutableCollection<VGSFieldState>.mapToEncodedQuery(): String? {
    val jObjectData = JSONObject()
    for (entry in this) {
        jObjectData.put(entry.alias, entry.content)
    }
    return jObjectData.toString()
}
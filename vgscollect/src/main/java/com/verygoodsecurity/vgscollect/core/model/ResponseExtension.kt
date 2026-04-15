package com.verygoodsecurity.vgscollect.core.model

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

// TODO: Refactor
/** @suppress */
internal fun String.toMutableMap(): Map<String, Any> {
    val resultMap = HashMap<String, Any>()
    when {
        isJSONObjectValid(this) -> {
            val json = JSONObject(this)
            return json.toMap()
        }
    }

    return resultMap
}

private fun isJSONObjectValid(target: String?): Boolean {
    if (target.isNullOrEmpty()) return false
    return try {
        JSONObject(target)
        true
    } catch (_: JSONException) {
        false
    }
}

@Throws(JSONException::class)
private fun JSONObject.toMap(): Map<String, Any> {
    val map: MutableMap<String, Any> =
        HashMap()
    val keysItr = this.keys()
    while (keysItr.hasNext()) {
        val key = keysItr.next()
        var value = this[key]
        if (value is JSONArray) {
            value = value.toList()
        } else if (value is JSONObject) {
            value = value.toMap()
        }
        map[key] = value
    }
    return map
}

@Throws(JSONException::class)
private fun JSONArray.toList(): List<Any> {
    val list: MutableList<Any> = ArrayList()
    for (i in 0 until this.length()) {
        var value = this[i]
        if (value is JSONArray) {
            value = value.toList()
        } else if (value is JSONObject) {
            value = value.toMap()
        }
        list.add(value)
    }
    return list
}
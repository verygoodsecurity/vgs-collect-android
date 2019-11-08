package com.verygoodsecurity.vgscollect.core.model

import org.json.JSONObject

internal fun String.parseVGSResponse():Map<String, String> {
    val map = mutableMapOf<String, String>()
    val parent = JSONObject(this)
    if(parent.has("json") && !parent.isNull("json")) {
        val jsonObj = parent.getJSONObject("json")
        val keys = jsonObj.keys()
        while(keys.hasNext()) {
            val key = keys.next()
            map[key] = jsonObj.optString(key)
        }
    }

    return map
}
package com.verygoodsecurity.vgscollect.util



/** @suppress */
fun MutableMap<String, Any>.deepMerge(
    newMap: Map<String, Any>
): Map<String,Any> {
    newMap.keys.forEach { key ->
        val value = newMap[key]
        if(value != null) {
            if (value is Map<*, *> && this[key] is Map<*, *>) {
                val originalChild = this[key] as MutableMap<String, Any>
                val newChild = newMap[key] as Map<String, Any>
                this[key] = originalChild.deepMerge(newChild)
            } else {
                put(key, value)
            }
        }
    }

    return this
}



/** @suppress */
fun MutableMap<String, Any>.mapToMap(): MutableMap<String,Any> {
    val resultedMap = mutableMapOf<String, Any>()

    this.keys.forEach { key ->
        val rootValue = this[key]
        val keyList = splitKeys(key)

        if(rootValue != null) {
            if (isMap(rootValue)) {
                processItemAsMap(
                    resultedMap,
                    (rootValue as MutableMap<String, Any>),
                    keyList
                )
            } else {
                processItem(
                    resultedMap,
                    rootValue,
                    keyList
                )
            }
        }
    }

    return resultedMap
}

fun processItem(
    rootMap: MutableMap<String, Any>,
    rootValue: Any,
    keyList: List<String>
) {
    var lastMap:MutableMap<String,Any> = rootMap
    for(i in keyList.indices) {
        val innerKey = keyList[i]

        val innerValue = lastMap[innerKey]
        when {
            i == keyList.size -1 -> lastMap[innerKey] = rootValue
            isMap(innerValue)  -> lastMap = lastMap[innerKey] as MutableMap<String, Any>
            else -> {
                lastMap = createNewMap(lastMap, innerKey)
            }
        }
    }
}


fun processItemAsMap(
    rootMap: MutableMap<String, Any>,
    rootValue: MutableMap<String, Any>,
    keyList: List<String>
) {
    var lastMap:MutableMap<String,Any> = rootMap

    for(i in keyList.indices) {
        val innerKey = keyList[i]

        if(i == keyList.size -1) {
            lastMap[innerKey] = rootValue.mapToMap()
        } else {
            lastMap = createNewMap(lastMap, innerKey)
        }
    }
}

private fun splitKeys(key: String): List<String> {
    return key.split(".").filter {
        it.isNotEmpty()
    }
}

private fun isMap(value:Any?):Boolean {
    return value is Map<*,*>
}

private fun createNewMap(
    lastMap: MutableMap<String, Any>,
    innerKey: String
):MutableMap<String, Any> {
    val map = HashMap<String,Any>()
    lastMap[innerKey] = map
    return map
}

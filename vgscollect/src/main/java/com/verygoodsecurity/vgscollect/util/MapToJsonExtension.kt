package com.verygoodsecurity.vgscollect.util

import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import org.json.JSONArray
import org.json.JSONObject

internal fun MutableCollection<VGSFieldState>.mapUsefulPayloads(
    userData: HashMap<String, Any>? = null
): Map<String,Any>? {

    val map = userData ?: HashMap()

    this.forEach { s->
        val contentData = s.content?.run {
            if(this is FieldContent.CardNumberContent) {
                rawData?:data
            } else {
                data
            }
        }
        if(!contentData.isNullOrEmpty()) {
            s.fieldName?.split(".")?.
                filter {
                    it.isNotEmpty()
                }?.
                mapStr(map, contentData)
        }
    }

    return map
}

private fun List<String>.mapStr(
    userData: HashMap<String, Any>,
    content: String
):Map<String,Any> {

    var lastChainMap:HashMap<String, Any> = userData
    for (i in 0 until size) {
        val key = this[i]
        if(i == size-1) {
            lastChainMap[key] = content
        } else {
            if(lastChainMap.containsKey(key)) {
                val value = lastChainMap[key]

                when(value) {
                    is Map<*, *> -> {
                        lastChainMap = value as HashMap<String, Any>
                    }
                    is Array<*> -> {
                        val mutL = value.toMutableList()

                        var containtsТestedItems = false
                        if(i+1 <= size-1) {
                            val nextKey = this[i+1]
                            mutL.forEach {
                                if(it != null && it is HashMap<*,*> && it.containsKey(nextKey)) {
                                    containtsТestedItems = true
                                    lastChainMap = it as HashMap<String, Any>
                                }
                            }
                        }
                        if(!containtsТestedItems) {
                            val map = HashMap<String, Any>()
                            mutL.add(map)
                            lastChainMap[key] = mutL
                            lastChainMap = map
                        }

                    }
                    is Collection<*> -> {
                        val mutL = value.toMutableList()

                        var containtsТestedItems = false
                        if(i+1 <= size-1) {
                            val nextKey = this[i+1]
                            mutL.forEach {
                                if(it != null && it is HashMap<*,*> && it.containsKey(nextKey)) {
                                    containtsТestedItems = true
                                    lastChainMap = it as HashMap<String, Any>
                                }
                            }
                        }
                        if(!containtsТestedItems) {
                            val map = HashMap<String, Any>()
                            mutL.add(map)
                            lastChainMap[key] = mutL
                            lastChainMap = map
                        }

                    }
                    else -> {
                        val map = HashMap<String, Any>()
                        lastChainMap[key] = map
                        lastChainMap = map
                    }
                }
            } else {
                lastChainMap = key.maps(lastChainMap)
            }
        }
    }
    return userData
}

private fun String.maps(m:HashMap<String,Any>):HashMap<String,Any>  {
    val map = HashMap<String, Any>()

    m[this] = map

    return map
}






internal fun Map<*, *>.mapMapToJSON():JSONObject {
    val jObjectData = JSONObject()

    this.forEach { entry->
        val key:String = entry.key.toString()
        when(entry.value) {
            is String -> jObjectData.put(key, entry.value)
            is Int -> jObjectData.put(key, entry.value as Int)
            is Long -> jObjectData.put(key, entry.value as Long)
            is Char -> jObjectData.put(key, entry.value as Char)
            is Float -> jObjectData.put(key, entry.value)
            is Double -> jObjectData.put(key, entry.value as Double)
            is Map<*, *> -> {
                val j = (entry.value as Map<*, *>).mapMapToJSON()
                jObjectData.put(key, j)
            }
            is Array<*> -> {
                val array = (entry.value as Array<*>).mapArrToJSON()

                jObjectData.put(key, array)
            }
            is Collection<*> -> {
                val array = (entry.value as Collection<*>).mapCollectionToJSON()

                jObjectData.put(key, array)
            }
        }
    }
    return jObjectData
}

private fun Collection<*>.mapCollectionToJSON():JSONArray {
    val array = JSONArray()

    this.forEach { entry->
        when(entry) {
            is String -> array.put(entry)
            is Int -> array.put(entry)
            is Char -> array.put(entry)
            is Long -> array.put(entry)
            is Float -> array.put(entry)
            is Double -> array.put(entry)
            is Map<*, *> -> {
                val obj = entry.mapMapToJSON()
                array.put(obj)
            }
            is Array<*> -> {
                val array2 = entry.mapArrToJSON()
                array.put(array2)
            }
            is Collection<*> -> {
                val array2 = entry.mapCollectionToJSON()
                array.put(array2)
            }
        }
    }

    return array
}

private fun Array<*>.mapArrToJSON():JSONArray {
    val array = JSONArray()

    this.forEach { entry->
        when(entry) {
            is String -> array.put(entry)
            is Char -> array.put(entry)
            is Int -> array.put(entry)
            is Long -> array.put(entry)
            is Float -> array.put(entry)
            is Double -> array.put(entry)
            is Map<*, *> -> {
                val obj = entry.mapMapToJSON()
                array.put(obj)
            }
            is Array<*> -> {
                val array2 = entry.mapArrToJSON()
                array.put(array2)
            }
            is Collection<*> -> {
                val array2 = entry.mapCollectionToJSON()
                array.put(array2)
            }
        }
    }

    return array
}


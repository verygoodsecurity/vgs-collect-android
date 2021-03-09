package com.verygoodsecurity.vgscollect.util.extension

internal fun <K, V> MutableMap<K, V>.putIfAbsentSafe(key: K?, value: V): V? {
    return key?.let {
        var v: V? = get(key)
        if (v == null) {
            v = put(key, value)
        }
        return@let v
    }
}
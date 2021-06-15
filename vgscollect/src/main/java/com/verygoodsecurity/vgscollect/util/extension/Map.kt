package com.verygoodsecurity.vgscollect.util.extension

import com.verygoodsecurity.vgscollect.core.model.map.FlatMap
import org.json.JSONObject

fun <K, V> Map<K, V>.toJSON(): JSONObject = try {
    JSONObject(this)
} catch (e: Exception) {
    JSONObject()
}

internal fun <K, V> MutableMap<K, V>.putIfAbsentCompat(key: K?, value: V): V? {
    return key?.let {
        if (get(key) == null) {
            put(key, value)
            return@let value
        }
        return@let null
    }
}

internal fun Map<String, Any>.toFlatMap(allowParseArrays: Boolean): FlatMap {
    return FlatMap(allowParseArrays).apply {
        this@toFlatMap.forEach { (key, value) ->
            this.set(key, value)
        }
    }
}

internal fun Iterable<Pair<String, Any>>.toFlatMap(allowParseArrays: Boolean): FlatMap {
    return toMap().toFlatMap(allowParseArrays)
}

@Suppress("UNCHECKED_CAST")
internal fun MutableMap<String, Any>.deepMerge(
    source: Map<String, Any>,
    policy: ArrayMergePolicy
): Map<String, Any> {
    source.forEach { (key, value) ->
        when {
            value is Map<*, *> && this[key] is Map<*, *> -> {
                val sourceValue = value as Map<String, Any>
                val targetValue = (this[key] as Map<String, Any>).toMutableMap()
                this[key] = targetValue.deepMerge(sourceValue, policy)
            }
            value is ArrayList<*> && this[key] is ArrayList<*> -> {
                val sourceValue = value as ArrayList<Any?>
                val targetValue = (this[key] as ArrayList<Any?>)
                this[key] = targetValue.deepMerge(sourceValue, policy)
            }
            else -> this[key] = value
        }
    }
    return this
}
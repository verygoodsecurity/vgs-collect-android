package com.verygoodsecurity.vgscollect.util.extension

internal fun <K, V> MutableMap<K, V>.putIfAbsentSafe(key: K?, value: V): V? {
    return key?.let {
        if (get(key) == null) {
            put(key, value)
            return@let value
        }
        return@let null
    }
}

@Suppress("UNCHECKED_CAST")
fun MutableMap<String, Any>.deepMerge(
    source: Map<String, Any>,
    policy: ArrayMergePolicy = ArrayMergePolicy.OVERWRITE
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
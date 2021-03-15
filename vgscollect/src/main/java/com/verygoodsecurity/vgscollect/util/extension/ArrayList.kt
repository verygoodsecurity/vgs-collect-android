package com.verygoodsecurity.vgscollect.util.extension

import com.verygoodsecurity.vgscollect.core.model.VGSArrayMergePolicy

internal fun <T> arrayListOfNulls(maxIndex: Int): ArrayList<T?> {
    val result = ArrayList<T?>(maxIndex)
    for (i in 0..maxIndex) {
        result.add(null)
    }
    return result
}

internal inline infix fun <reified T : Any> ArrayList<T?>.merge(source: ArrayList<T?>): ArrayList<T?> {
    val result = arrayListOfNulls<T>(this.size.coerceAtLeast(source.size).dec())
    for (i in 0 until result.size) {
        result[i] = this.getOrNull(i)
        source.getOrNull(i)?.let { result[i] = it }
    }
    return result
}

@Suppress("UNCHECKED_CAST")
internal fun ArrayList<Any?>.deepMerge(
    source: ArrayList<Any?>,
    policy: VGSArrayMergePolicy
): ArrayList<Any?> {
    return when (policy) {
        VGSArrayMergePolicy.OVERWRITE -> source
        VGSArrayMergePolicy.MERGE -> {
            source.forEachIndexed { index, value ->
                when {
                    value is Map<*, *> && this.getOrNull(index) is Map<*, *> -> { // Target and source values are maps, try to merge
                        val sourceValue = value as Map<String, Any>
                        val targetValue = (this[index] as Map<String, Any>).toMutableMap()
                        this[index] = targetValue.deepMerge(sourceValue, policy)
                    }
                    value is Map<*, *> -> this.setOrAdd(value, index) // Source value is map, replace target value
                    else -> value?.let { this.add(it) } // Add source value if not null
                }
            }
            this
        }
    }
}

internal fun <T> ArrayList<T>.setOrAdd(value: T, index: Int) {
    try {
        set(index, value)
    } catch (e: Exception) {
        add(value)
    }
}
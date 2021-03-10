package com.verygoodsecurity.vgscollect.util.extension

import kotlin.jvm.Throws

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
fun ArrayList<Any?>.deepMerge(
    source: ArrayList<Any?>,
    policy: ArrayMergePolicy = ArrayMergePolicy.OVERWRITE
): ArrayList<Any?> {
    return when (policy) {
        ArrayMergePolicy.OVERWRITE -> source
        ArrayMergePolicy.MERGE -> {
            source.forEachIndexed { index, value ->
                when {
                    value is Map<*, *> && this.getOrNull(index) is Map<*, *> -> {
                        val sourceValue = value as Map<String, Any>
                        val targetValue = (this[index] as Map<String, Any>).toMutableMap()
                        this[index] = targetValue.deepMerge(sourceValue, policy)
                    }
                    value is Map<*, *> -> this.setOrAdd(index, value)
                    else -> this.add(value)
                }
            }
            this
        }
    }
}

@Throws(IndexOutOfBoundsException::class)
fun <T> ArrayList<T>.setOrAdd(index: Int, value: T) {
    try {
        set(index, value)
    } catch (e: Exception) {
        add(index, value)
    }
}

enum class ArrayMergePolicy {

    OVERWRITE,
    MERGE
}
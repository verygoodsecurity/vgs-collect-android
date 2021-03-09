package com.verygoodsecurity.vgscollect.util.extension

internal fun <T> arrayListOfNulls(capacity: Int): ArrayList<T?> {
    val result = ArrayList<T?>(capacity)
    for (i in 0..capacity) {
        result.add(null)
    }
    return result
}

internal inline infix fun <reified T : Any> ArrayList<T?>.merge(target: ArrayList<T?>): ArrayList<T?> {
    val result = arrayListOfNulls<T>(this.size.coerceAtLeast(target.size).dec())
    for (i in 0 until result.size) {
        result[i] = this.getOrNull(i)
        target.getOrNull(i)?.let { result[i] = it }
    }
    return result
}
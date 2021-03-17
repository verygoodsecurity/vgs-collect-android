package com.verygoodsecurity.vgscollect.core.model.map

import com.verygoodsecurity.vgscollect.util.extension.arrayListOfNulls
import com.verygoodsecurity.vgscollect.util.extension.merge
import com.verygoodsecurity.vgscollect.util.extension.putIfAbsentCompat

@Suppress("UNCHECKED_CAST")
class FlatMap constructor(private val allowParseArrays: Boolean = true) {

    val structuredData: MutableMap<String, Any> by lazy { mutableMapOf() }

    fun set(key: String, value: Any): Any? {
        val keys = key.split(DOT_SEPARATOR).map { Key.create(it, allowParseArrays) }.toMutableList()
        return if (keys.any { !it.isValid }) null else set(structuredData, keys, value)
    }

    private fun set(target: MutableMap<String, Any>, keys: MutableList<Key>, value: Any): Any? {
        val key = keys.removeFirst()
        val v = if (keys.isEmpty()) value else null
        val nestedTarget = when (key) {
            is Key.ArrayKey -> addArray(target, key, v)
            is Key.ObjectKey -> addObject(target, key, v)
        }
        return if (nestedTarget == null) value else set(nestedTarget, keys, value)
    }

    private fun addObject(
        target: MutableMap<String, Any>,
        key: Key.ObjectKey,
        value: Any?
    ): MutableMap<String, Any>? {
        target.putIfAbsentCompat(key.value, value ?: mutableMapOf<String, Any>())
        return target[key.value] as? MutableMap<String, Any>
    }

    private fun addArray(
        target: MutableMap<String, Any>,
        key: Key.ArrayKey,
        value: Any?
    ): MutableMap<String, Any>? {
        val array = (target[key.value] ?: arrayListOfNulls<Any?>(key.position)) as ArrayList<Any?>
        val currentValue = value ?: (array.getOrNull(key.position) ?: mutableMapOf<String, Any>())
        val currentArray =
            arrayListOfNulls<Any>(key.position).apply { this[key.position] = currentValue }

        target[key.value] = array merge currentArray

        return currentValue as? MutableMap<String, Any>
    }

    override fun toString(): String {
        return structuredData.toString()
    }

    companion object {

        private const val DOT_SEPARATOR = "."
    }
}
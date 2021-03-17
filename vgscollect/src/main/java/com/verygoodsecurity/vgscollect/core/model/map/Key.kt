package com.verygoodsecurity.vgscollect.core.model.map

import com.verygoodsecurity.vgscollect.util.extension.substringOrNull

sealed class Key {

    abstract val value: String

    abstract val isValid: Boolean

    data class ObjectKey constructor(override val value: String) : Key() {

        override val isValid: Boolean
            get() = value.isNotEmpty() && value.isNotBlank()
    }

    data class ArrayKey constructor(val rawValue: String) : Key() {

        override val value: String = rawValue.substringBefore(START_BRACKET)

        override val isValid: Boolean
            get() = value.isNotEmpty() && value.isNotBlank() && position >= 0

        val position: Int = rawValue.substringOrNull(
            rawValue.indexOf(START_BRACKET),
            rawValue.indexOf(END_BRACKET)
        )?.toIntOrNull() ?: -1

        companion object {

            private const val START_BRACKET = "["
            private const val END_BRACKET = "]"

            fun isArrayKey(key: String): Boolean {
                return key.contains(START_BRACKET) && key.contains(END_BRACKET)
            }
        }
    }

    companion object {

        fun create(key: String, allowParseArray: Boolean): Key =
            if (allowParseArray && ArrayKey.isArrayKey(key)) {
                ArrayKey(key)
            } else {
                ObjectKey(key)
            }
    }
}
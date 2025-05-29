package com.verygoodsecurity.vgscollect.core.storage

import com.verygoodsecurity.vgscollect.core.model.state.FieldContent.CardNumberContent
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent.DateContent
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent.SSNContent
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.widget.compose.core.BaseFieldState

internal class StorageFieldState(
    val fieldName: String,
    val isValid: Boolean,
    val data: String
)

internal fun List<BaseFieldState>.mapStorageFieldState(): List<StorageFieldState> {
    return this.mapNotNull { state ->
        state.fieldName?.let {
            StorageFieldState(
                it,
                state.isValid,
                state.text
            )
        }
    }
}

internal fun MutableCollection<VGSFieldState>.mapStorageFieldState(): List<StorageFieldState> {
    val result = mutableListOf<StorageFieldState>()
    this.forEach { state ->
        val fieldName = state.fieldName
        if (fieldName.isNullOrEmpty()) {
            return@forEach
        }
        when (val c = state.content) {
            is CardNumberContent, is SSNContent -> {
                (c.rawData ?: c.data)?.let { data ->
                    result.add(
                        StorageFieldState(
                            fieldName = fieldName,
                            isValid = state.isValid,
                            data = data
                        )
                    )
                }
            }

            is DateContent -> {
                val data = (c.rawData ?: c.data) ?: return@forEach
                c.serializers?.forEach {
                    val data = it.serialize(data, c.dateFormat)
                    result.addAll(data.map {
                        StorageFieldState(
                            fieldName = it.first,
                            isValid = state.isValid,
                            data = it.second
                        )
                    })
                } ?: result.add(
                    StorageFieldState(
                        fieldName = fieldName,
                        isValid = state.isValid,
                        data = data
                    )
                )
            }

            else -> {
                c?.data?.let { data ->
                    result.add(
                        StorageFieldState(
                            fieldName = fieldName,
                            isValid = state.isValid,
                            data = data
                        )
                    )
                }
            }
        }
    }
    return result
}
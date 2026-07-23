package com.verygoodsecurity.vgscollect.core.storage

import com.verygoodsecurity.vgscollect.VGSCollectLogger
import com.verygoodsecurity.vgscollect.core.model.network.VGSError
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent.CardNumberContent
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent.DateContent
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent.SSNContent
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.VgsExpiryTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.core.BaseFieldState

internal class StorageFieldState(
    val fieldName: String,
    val data: String,
    val isValid: Boolean,
)

internal fun List<BaseFieldState>.mapToStorageFieldState(): List<StorageFieldState> {
    val result = mutableListOf<StorageFieldState>()
    this.forEach { state ->
        if (state.fieldName.isBlank()) {
            VGSCollectLogger.warn(
                state::class.simpleName.toString(),
                VGSError.FIELD_NAME_NOT_SET.message
            )
            return@forEach
        }
        if (state is VgsExpiryTextFieldState && state.serializer != null) {
            state.serializer.getSerialized(
                state.text,
                state.inputDateFormat,
                state.outputDateFormat
            ).forEach { (fieldName, data) ->
                result.add(
                    StorageFieldState(
                        fieldName = fieldName,
                        data = data,
                        isValid = state.isValid,
                    )
                )
            }
        } else {
            result.add(
                StorageFieldState(
                    fieldName = state.fieldName,
                    data = state.getOutputText(),
                    isValid = state.isValid,
                )
            )
        }
    }
    return result
}

internal fun MutableCollection<VGSFieldState>.mapToStorageFieldState(): List<StorageFieldState> {
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
                c.serializers?.forEach { serializer ->
                    val data = serializer.serialize(data, c.dateFormat)
                    result.addAll(data.map {
                        StorageFieldState(
                            fieldName = it.first,
                            data = it.second,
                            isValid = state.isValid,
                        )
                    })
                } ?: result.add(
                    StorageFieldState(
                        fieldName = fieldName,
                        data = data,
                        isValid = state.isValid,
                    )
                )
            }

            else -> {
                c?.data?.let { data ->
                    result.add(
                        StorageFieldState(
                            fieldName = fieldName,
                            data = data,
                            isValid = state.isValid,
                        )
                    )
                }
            }
        }
    }
    return result
}
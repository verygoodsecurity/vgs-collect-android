package com.verygoodsecurity.vgscollect.core.storage

import com.verygoodsecurity.vgscollect.VGSCollectLogger
import com.verygoodsecurity.vgscollect.core.model.network.VGSError
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent.CardNumberContent
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent.DateContent
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent.SSNContent
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.util.extension.FIELD_NAME_KEY
import com.verygoodsecurity.vgscollect.util.extension.FORMAT_KEY
import com.verygoodsecurity.vgscollect.util.extension.STORAGE_KEY
import com.verygoodsecurity.vgscollect.util.extension.TOKENIZATION_REQUIRED_KEY
import com.verygoodsecurity.vgscollect.util.extension.VALUE_KEY
import com.verygoodsecurity.vgscollect.widget.compose.state.VgsExpiryTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.core.BaseFieldState

internal class StorageFieldState(
    val fieldName: String,
    val isValid: Boolean,
    val data: String
)

internal fun List<BaseFieldState>.mapStorageFieldState(): List<StorageFieldState> {
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
                        fieldName,
                        state.isValid,
                        data
                    )
                )
            }
        } else {
            result.add(
                StorageFieldState(
                    state.fieldName,
                    state.isValid,
                    state.getOutputText()
                )
            )
        }
    }
    return result
}

internal fun List<BaseFieldState>.mapToTokenizationData(): List<Map<String, Any>> {
    val result = mutableListOf<Map<String, Any>>()
    this.forEach { state ->
        if (state.fieldName.isBlank()) return@forEach
        val pairs = if (state is VgsExpiryTextFieldState && state.serializer != null) {
            state.serializer.getSerialized(state.text, state.inputDateFormat, state.outputDateFormat)
        } else {
            listOf(state.fieldName to state.getOutputText())
        }
        val config = state.tokenizationConfig
        pairs.forEach { (fieldName, value) ->
            result.add(
                mapOf(
                    TOKENIZATION_REQUIRED_KEY to (config != null),
                    VALUE_KEY to value,
                    FORMAT_KEY to (config?.format?.name ?: ""),
                    STORAGE_KEY to (config?.storage?.name ?: ""),
                    FIELD_NAME_KEY to fieldName
                )
            )
        }
    }
    return result
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
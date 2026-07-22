package com.verygoodsecurity.vgscollect.util.extension

import com.verygoodsecurity.vgscollect.VGSCollectLogger
import com.verygoodsecurity.vgscollect.core.model.network.VGSError
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent.CardNumberContent
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent.DateContent
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent.SSNContent
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.VgsExpiryTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.core.BaseFieldState

internal const val TOKENIZATION_PATH = "/tokens"
internal const val ALIASES_PATH = "/aliases"
internal const val DATA_KEY = "data"
internal const val TOKENIZATION_REQUIRED_KEY = "is_required_tokenization"
internal const val VALUE_KEY = "value"
internal const val FORMAT_KEY = "format"
internal const val STORAGE_KEY = "storage"
internal const val FIELD_NAME_KEY = "fieldName"
internal const val ALIASES_KEY = "aliases"
internal const val ALIAS_KEY = "alias"

internal fun MutableCollection<VGSFieldState>.mapToTokenizationData(): List<Map<String, Any>> {
    val result = mutableListOf<Map<String, Any>>()
    this.forEach { state ->
        val fieldName = state.fieldName ?: ""
        if (fieldName.isBlank()) {
            VGSCollectLogger.warn(
                state::class.simpleName.toString(),
                VGSError.FIELD_NAME_NOT_SET.message
            )
            return@forEach
        }
        result.addAll(getData(fieldName, state.content).map {
            mapOf(
                TOKENIZATION_REQUIRED_KEY to (state.content?.isEnabledTokenization ?: false),
                VALUE_KEY to it.second,
                FORMAT_KEY to (state.content?.vaultAliasFormat?.name ?: ""),
                STORAGE_KEY to (state.content?.vaultStorage?.name ?: ""),
                FIELD_NAME_KEY to it.first
            )
        })
    }
    return result
}

internal fun List<BaseFieldState>.mapToTokenizationData(): List<Map<String, Any>> {
    val result = mutableListOf<Map<String, Any>>()
    this.forEach { state ->
        if (state.fieldName.isBlank()) {
            VGSCollectLogger.warn(
                state::class.simpleName.toString(),
                VGSError.FIELD_NAME_NOT_SET.message
            )
            return@forEach
        }
        val pairs = if (state is VgsExpiryTextFieldState && state.serializer != null) {
            state.serializer.getSerialized(
                state.text,
                state.inputDateFormat,
                state.outputDateFormat
            )
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

private fun getData(
    fieldName: String,
    content: FieldContent?
): List<Pair<String, String>> = when (content) {
    is DateContent -> handleDateContent(fieldName, content)
    is CardNumberContent, is SSNContent -> listOf(fieldName to (content.rawData ?: content.data ?: ""))
    else -> listOf(fieldName to (content?.data ?: ""))
}

private fun handleDateContent(
    fieldName: String,
    content: DateContent
): List<Pair<String, String>> {
    val result = mutableListOf<Pair<String, String>>()
    val data = (content.rawData ?: content.data!!)
    content.serializers?.forEach {
        result.addAll(it.serialize(data, content.dateFormat))
    } ?: result.add(fieldName to data)
    return result
}

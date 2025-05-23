package com.verygoodsecurity.vgscollect.util.extension

import com.verygoodsecurity.vgscollect.core.model.state.FieldContent.DateContent
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent.CardNumberContent
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent.SSNContent
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.view.core.serializers.VGSDateRangeSeparateSerializer
import com.verygoodsecurity.vgscollect.view.core.serializers.VGSExpDateSeparateSerializer

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

internal fun VGSFieldState.toTokenizationData(): List<Map<String, Any>> {
    return getData(fieldName ?: "", content).map {
        mapOf(
            TOKENIZATION_REQUIRED_KEY to (content?.isEnabledTokenization ?: false),
            VALUE_KEY to it.second,
            FORMAT_KEY to (content?.vaultAliasFormat?.name ?: ""),
            STORAGE_KEY to (content?.vaultStorage?.name ?: ""),
            FIELD_NAME_KEY to it.first
        )
    }
}

private fun getData(
    fieldName: String,
    content: FieldContent?
): List<Pair<String, String>> = when (content) {
    is DateContent -> {
        handleDateContent(fieldName, content)
    }
    is CardNumberContent, is SSNContent -> {
        listOf(fieldName to (content.rawData ?: content.data ?: ""))
    }
    else -> listOf(fieldName to (content?.data ?: ""))
}

private fun handleDateContent(
    fieldName: String,
    content: DateContent
): List<Pair<String, String>> {
    val result = mutableListOf<Pair<String, String>>()
    val data = (content.rawData ?: content.data!!)
    content.serializers?.forEach {
        if (it is VGSExpDateSeparateSerializer) {
            result.addAll(
                it.serialize(VGSExpDateSeparateSerializer.Params(data, content.dateFormat))
            )
        }
        if (it is VGSDateRangeSeparateSerializer) {
            result.addAll(
                it.serialize(VGSDateRangeSeparateSerializer.Params(data, content.dateFormat))
            )
        }
    } ?: result.add(fieldName to data)
    return result
}

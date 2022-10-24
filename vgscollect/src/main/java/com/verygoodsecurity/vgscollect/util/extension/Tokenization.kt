package com.verygoodsecurity.vgscollect.util.extension

import android.util.Log
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.view.core.serializers.VGSExpDateSeparateSerializer

internal const val TOKENIZATION_PATH = "/tokens"
internal const val DATA_KEY = "data"
internal const val TOKENIZATION_REQUIRED_KEY = "is_required_tokenization"
internal const val VALUE_KEY = "value"
internal const val FORMAT_KEY = "format"
internal const val STORAGE_KEY = "storage"
internal const val FIELD_NAME_KEY = "fieldName"
internal const val ALIASES_KEY = "aliases"
internal const val ALIAS_KEY = "alias"

internal fun VGSFieldState.toTokenizationMap(): MutableMap<String, Any> {
    val data = content?.data ?: ""
    val isEnabledTokenization = content?.isEnabledTokenization ?: false
    val format = content?.vaultAliasFormat?.name ?: ""
    val storage = content?.vaultStorage?.name ?: ""
    val fieldName = fieldName ?: ""

    return mutableMapOf(
        TOKENIZATION_REQUIRED_KEY to isEnabledTokenization,
        VALUE_KEY to data,
        FORMAT_KEY to format,
        STORAGE_KEY to storage,
        FIELD_NAME_KEY to fieldName
    )
}

internal fun FieldContent.CreditCardExpDateContent.toTokenizationMap(fieldName: String): List<Map<String, Any>> {
    Log.d("Test", "CreditCardExpDateContent::isEnabledTokenization = $isEnabledTokenization")
    return handleExpirationDateContent(fieldName, this).map {
        mapOf(
            TOKENIZATION_REQUIRED_KEY to isEnabledTokenization,
            VALUE_KEY to it.second,
            FORMAT_KEY to vaultAliasFormat.name,
            STORAGE_KEY to vaultStorage.name,
            FIELD_NAME_KEY to it.first
        )
    }
}

private fun handleExpirationDateContent(
    fieldName: String,
    content: FieldContent.CreditCardExpDateContent
): List<Pair<String, String>> {
    val result = mutableListOf<Pair<String, String>>()
    val data = (content.rawData ?: content.data!!)
    if (content.serializers != null) {
        content.serializers?.forEach {
            if (it is VGSExpDateSeparateSerializer) {
                result.addAll(
                    it.serialize(VGSExpDateSeparateSerializer.Params(data, content.dateFormat))
                )
            }
        }
    } else {
        result.add(fieldName to data)
    }
    return result
}

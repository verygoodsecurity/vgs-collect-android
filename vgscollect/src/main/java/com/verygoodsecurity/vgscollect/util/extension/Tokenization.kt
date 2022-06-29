package com.verygoodsecurity.vgscollect.util.extension

import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState

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

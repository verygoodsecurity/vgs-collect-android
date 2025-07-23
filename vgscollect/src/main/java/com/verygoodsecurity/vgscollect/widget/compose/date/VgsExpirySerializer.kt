package com.verygoodsecurity.vgscollect.widget.compose.date

import com.verygoodsecurity.vgscollect.view.core.serializers.VGSExpDateSeparateSerializer

class VgsExpirySerializer(monthFieldName: String, yearFieldName: String) {

    private val legacySerializer = VGSExpDateSeparateSerializer(monthFieldName, yearFieldName)

    fun getSerialized(
        text: String, // Raw text without formatting
        inputFormat: VgsExpiryDateFormat,
        outputFormat: VgsExpiryDateFormat,
    ): List<Pair<String, String>> {
        return inputFormat.convert(text, outputFormat)?.let {
            legacySerializer.serialize(it, outputFormat.dateFormat)
        } ?: emptyList()
    }
}
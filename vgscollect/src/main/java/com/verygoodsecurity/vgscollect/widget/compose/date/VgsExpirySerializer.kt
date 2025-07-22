package com.verygoodsecurity.vgscollect.widget.compose.date

import com.verygoodsecurity.vgscollect.view.core.serializers.VGSExpDateSeparateSerializer
import com.verygoodsecurity.vgscollect.widget.compose.util.format

class VgsExpirySerializer(monthFieldName: String, yearFieldName: String) {

    private val legacySerializer = VGSExpDateSeparateSerializer(monthFieldName, yearFieldName)

    fun getSerialized(
        text: String, // Raw text without formatting
        inputFormat: VgsExpiryDateFormat,
    ): List<Pair<String, String>> {
        return legacySerializer.serialize(text.format(inputFormat.mask), inputFormat.dateFormat)
    }
}
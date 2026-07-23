package com.verygoodsecurity.vgscollect.widget.compose.date

import com.verygoodsecurity.vgscollect.view.core.serializers.VGSExpDateSeparateSerializer

/**
 * Splits a card expiry value into separate month and year JSON fields on submit.
 *
 * Pass an instance to [com.verygoodsecurity.vgscollect.widget.compose.state.rememberVgsExpiryTextFieldState]
 * when your VGS route expects the month and year in distinct keys instead of
 * a single combined value. Example payload:
 *
 * ```
 * {
 *   "card": {
 *     "expiry": {
 *       "month": "12",
 *       "year": "27"
 *     }
 *   }
 * }
 * ```
 *
 * @param monthFieldName JSON key the month is sent under.
 * @param yearFieldName JSON key the year is sent under.
 */
class VgsExpirySerializer(monthFieldName: String, yearFieldName: String) {

    private val legacySerializer = VGSExpDateSeparateSerializer(monthFieldName, yearFieldName)

    /**
     * Serialises [text] into a list of `(fieldName, value)` pairs ready for submission.
     *
     * @param text raw text without formatting.
     * @param inputFormat format used to parse [text].
     * @param outputFormat format used to render the parsed date before splitting.
     */
    fun getSerialized(
        text: String,
        inputFormat: VgsExpiryDateFormat,
        outputFormat: VgsExpiryDateFormat,
    ): List<Pair<String, String>> {
        return inputFormat.convert(text, outputFormat)?.let {
            legacySerializer.serialize(it, outputFormat.dateFormat)
        } ?: emptyList()
    }
}
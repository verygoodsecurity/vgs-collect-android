package com.verygoodsecurity.vgscollect.core.model

import org.json.JSONArray
import org.json.JSONObject

internal data class CardAttributesConfig(
    val isEnabled: Boolean,
    val filters: List<String>,
) {

    companion object {

        private const val KEY_CONFIG = "config"
        private const val KEY_CARD_ATTRIBUTES = "cardAttributes"
        private const val KEY_ENABLE = "enable"
        private const val KEY_PARAMETERS = "parameters"

        fun parse(response: String?): CardAttributesConfig? {
            return try {
                val cardAttributes = JSONObject(response ?: throw Exception())
                    .getJSONObject(KEY_CONFIG)
                    .getJSONObject(KEY_CARD_ATTRIBUTES)

                val filters = cardAttributes.optJSONArray(KEY_PARAMETERS)?.toStringList().orEmpty()

                CardAttributesConfig(
                    isEnabled = cardAttributes.getBoolean(KEY_ENABLE),
                    filters = filters
                )
            } catch (_: Exception) {
                null
            }
        }

        private fun JSONArray.toStringList(): List<String> {
            return List(length()) { index -> optString(index) }.filter { it.isNotBlank() }
        }
    }
}
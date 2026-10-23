package com.verygoodsecurity.demoapp.tokenization.settings

import android.content.Context
import androidx.preference.PreferenceManager
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultAliasFormat
import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultStorageType
import com.verygoodsecurity.vgscollect.widget.compose.tokenization.VgsCardNumberTokenizationConfig
import com.verygoodsecurity.vgscollect.widget.compose.tokenization.VgsCardholderTokenizationConfig
import com.verygoodsecurity.vgscollect.widget.compose.tokenization.VgsExpiryTokenizationConfig

/** One field's options as chosen on the settings screen. */
data class FieldTokenizationSettings(
    val enabled: Boolean,
    val storage: VGSVaultStorageType,
    val format: VGSVaultAliasFormat,
)

/**
 * Tokenization options chosen on [TokenizationSettingsScreen], stored in the default shared preferences.
 *
 * Compose demos turn them into field configs: a disabled field gets no config and is sent as is.
 * Equal values mean nothing changed, so screens can use the instance as a `key`.
 */
data class TokenizationSettings(
    val cardHolder: FieldTokenizationSettings,
    val cardNumber: FieldTokenizationSettings,
    val expiry: FieldTokenizationSettings,
) {

    val cardHolderConfig: VgsCardholderTokenizationConfig?
        get() = cardHolder.takeIf { it.enabled }?.let { VgsCardholderTokenizationConfig(it.format, it.storage) }

    val cardNumberConfig: VgsCardNumberTokenizationConfig
        get() = VgsCardNumberTokenizationConfig(cardNumber.format, cardNumber.storage)

    val expiryConfig: VgsExpiryTokenizationConfig?
        get() = expiry.takeIf { it.enabled }?.let { VgsExpiryTokenizationConfig(it.format, it.storage) }

    companion object {

        /** SDK defaults of the Compose field configs. */
        val DEFAULT_CARD_HOLDER_FORMAT = VGSVaultAliasFormat.UUID
        val DEFAULT_CARD_NUMBER_FORMAT = VGSVaultAliasFormat.FPE_SIX_T_FOUR
        val DEFAULT_EXPIRY_FORMAT = VGSVaultAliasFormat.UUID
        val DEFAULT_STORAGE = VGSVaultStorageType.PERSISTENT

        fun read(context: Context): TokenizationSettings {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            fun field(
                enabledKey: Int?,
                storageKey: Int?,
                formatKey: Int,
                defaultFormat: VGSVaultAliasFormat
            ) = FieldTokenizationSettings(
                enabled = enabledKey == null || prefs.getBoolean(context.getString(enabledKey), true),
                storage = storageKey
                    ?.let { prefs.getString(context.getString(it), null) }
                    ?.let { name -> runCatching { VGSVaultStorageType.valueOf(name) }.getOrNull() }
                    ?: DEFAULT_STORAGE,
                format = prefs.getString(context.getString(formatKey), null)
                    ?.let { name -> runCatching { VGSVaultAliasFormat.valueOf(name) }.getOrNull() }
                    ?: defaultFormat,
            )
            return TokenizationSettings(
                cardHolder = field(
                    R.string.tokenization_card_holder_enabled_key,
                    R.string.tokenization_card_holder_storage_key,
                    R.string.tokenization_card_holder_alias_format_key,
                    DEFAULT_CARD_HOLDER_FORMAT
                ),
                cardNumber = field(
                    null,
                    null,
                    R.string.tokenization_card_number_alias_format_key,
                    DEFAULT_CARD_NUMBER_FORMAT
                ),
                expiry = field(
                    R.string.tokenization_expiry_enabled_key,
                    R.string.tokenization_expiry_storage_key,
                    R.string.tokenization_expiry_alias_format_key,
                    DEFAULT_EXPIRY_FORMAT
                ),
            )
        }
    }
}

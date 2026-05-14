package com.verygoodsecurity.vgscollect.widget.compose.tokenization

import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultAliasFormat
import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultStorageType

/**
 * Tokenization settings for a VGS Compose field.
 *
 * Pass an instance to a field's `rememberVgs…TextFieldState(... tokenizationConfig = ...)`
 * factory to opt that field into tokenization: on submit, the field's value
 * is replaced with an alias produced by the VGS vault.
 *
 * Use a field-specific subclass to get sensible defaults:
 * [VgsCardNumberTokenizationConfig], [VgsCvcTokenizationConfig],
 * [VgsCardHolderTokenizationConfig], [VgsSsnTokenizationConfig],
 * [VgsExpiryTokenizationConfig], [VgsTextFieldTokenizationConfig].
 *
 * @param format alias format the vault should produce.
 * @param storage whether the alias is stored persistently or only for the session.
 */
sealed class VgsTokenizationConfig(
    val format: VGSVaultAliasFormat,
    val storage: VGSVaultStorageType,
)

/**
 * Tokenization settings for card number fields.
 *
 * Default format preserves the first 6 and last 4 digits ([VGSVaultAliasFormat.FPE_SIX_T_FOUR]).
 */
class VgsCardNumberTokenizationConfig(
    format: VGSVaultAliasFormat = VGSVaultAliasFormat.FPE_SIX_T_FOUR,
    storage: VGSVaultStorageType = VGSVaultStorageType.PERSISTENT,
) : VgsTokenizationConfig(format, storage)

/**
 * Tokenization settings for CVC fields.
 *
 * Storage is fixed to [VGSVaultStorageType.VOLATILE] — CVC codes must never be stored persistently.
 */
class VgsCvcTokenizationConfig(
    format: VGSVaultAliasFormat = VGSVaultAliasFormat.NUM_LENGTH_PRESERVING,
) : VgsTokenizationConfig(format, VGSVaultStorageType.VOLATILE)

/**
 * Tokenization settings for cardholder name fields.
 */
class VgsCardHolderTokenizationConfig(
    format: VGSVaultAliasFormat = VGSVaultAliasFormat.UUID,
    storage: VGSVaultStorageType = VGSVaultStorageType.PERSISTENT,
) : VgsTokenizationConfig(format, storage)

/**
 * Tokenization settings for SSN fields.
 */
class VgsSsnTokenizationConfig(
    format: VGSVaultAliasFormat = VGSVaultAliasFormat.UUID,
    storage: VGSVaultStorageType = VGSVaultStorageType.PERSISTENT,
) : VgsTokenizationConfig(format, storage)

/**
 * Tokenization settings for expiry date fields.
 */
class VgsExpiryTokenizationConfig(
    format: VGSVaultAliasFormat = VGSVaultAliasFormat.UUID,
    storage: VGSVaultStorageType = VGSVaultStorageType.PERSISTENT,
) : VgsTokenizationConfig(format, storage)

/**
 * Tokenization settings for generic text fields.
 */
class VgsTextFieldTokenizationConfig(
    format: VGSVaultAliasFormat = VGSVaultAliasFormat.UUID,
    storage: VGSVaultStorageType = VGSVaultStorageType.PERSISTENT,
) : VgsTokenizationConfig(format, storage)

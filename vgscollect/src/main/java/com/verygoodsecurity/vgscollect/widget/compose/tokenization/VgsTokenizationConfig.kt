package com.verygoodsecurity.vgscollect.widget.compose.tokenization

import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultAliasFormat
import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultStorageType

/**
 * Base tokenization settings for a Compose field state.
 *
 * Pass an instance to a field state constructor to opt that field into tokenization.
 * Fields with no config are included in the tokenization request body but their values
 * are passed through unchanged (not replaced with aliases).
 *
 * Use the field-specific subclass to get correct defaults for each field type:
 * [VgsCardNumberTokenizationConfig], [VgsCvcTokenizationConfig],
 * [VgsCardHolderTokenizationConfig], [VgsSsnTokenizationConfig],
 * [VgsExpiryTokenizationConfig], [VgsTextFieldTokenizationConfig].
 *
 * @param format The alias format the VGS vault should produce.
 * @param storage Whether the alias is stored persistently or only for the session.
 */
sealed class VgsTokenizationConfig(
    val format: VGSVaultAliasFormat,
    val storage: VGSVaultStorageType,
)

/**
 * Tokenization settings for card number fields.
 *
 * Defaults to [VGSVaultAliasFormat.FPE_SIX_T_FOUR] to preserve the first 6 and last 4 digits,
 * matching the behavior of the view-based card number field.
 */
class VgsCardNumberTokenizationConfig(
    format: VGSVaultAliasFormat = VGSVaultAliasFormat.FPE_SIX_T_FOUR,
    storage: VGSVaultStorageType = VGSVaultStorageType.PERSISTENT,
) : VgsTokenizationConfig(format, storage)

/**
 * Tokenization settings for CVC fields.
 *
 * Storage is always [VGSVaultStorageType.VOLATILE] — CVC codes must never be stored persistently.
 * Defaults to [VGSVaultAliasFormat.NUM_LENGTH_PRESERVING], matching the view-based CVC field.
 */
class VgsCvcTokenizationConfig(
    format: VGSVaultAliasFormat = VGSVaultAliasFormat.NUM_LENGTH_PRESERVING,
) : VgsTokenizationConfig(format, VGSVaultStorageType.VOLATILE)

/**
 * Tokenization settings for cardholder name fields.
 *
 * Defaults to [VGSVaultAliasFormat.UUID] / [VGSVaultStorageType.PERSISTENT],
 * matching the behavior of the view-based cardholder name field.
 */
class VgsCardHolderTokenizationConfig(
    format: VGSVaultAliasFormat = VGSVaultAliasFormat.UUID,
    storage: VGSVaultStorageType = VGSVaultStorageType.PERSISTENT,
) : VgsTokenizationConfig(format, storage)

/**
 * Tokenization settings for SSN fields.
 *
 * Defaults to [VGSVaultAliasFormat.UUID] / [VGSVaultStorageType.PERSISTENT],
 * matching the behavior of the view-based SSN field.
 */
class VgsSsnTokenizationConfig(
    format: VGSVaultAliasFormat = VGSVaultAliasFormat.UUID,
    storage: VGSVaultStorageType = VGSVaultStorageType.PERSISTENT,
) : VgsTokenizationConfig(format, storage)

/**
 * Tokenization settings for expiry date fields.
 *
 * Defaults to [VGSVaultAliasFormat.UUID] / [VGSVaultStorageType.PERSISTENT],
 * matching the behavior of the view-based expiry date field.
 */
class VgsExpiryTokenizationConfig(
    format: VGSVaultAliasFormat = VGSVaultAliasFormat.UUID,
    storage: VGSVaultStorageType = VGSVaultStorageType.PERSISTENT,
) : VgsTokenizationConfig(format, storage)

/**
 * Tokenization settings for generic text fields.
 *
 * Defaults to [VGSVaultAliasFormat.UUID] / [VGSVaultStorageType.PERSISTENT],
 * matching the behavior of the view-based generic text field.
 */
class VgsTextFieldTokenizationConfig(
    format: VGSVaultAliasFormat = VGSVaultAliasFormat.UUID,
    storage: VGSVaultStorageType = VGSVaultStorageType.PERSISTENT,
) : VgsTokenizationConfig(format, storage)

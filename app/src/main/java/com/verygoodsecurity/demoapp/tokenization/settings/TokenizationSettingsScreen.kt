package com.verygoodsecurity.demoapp.tokenization.settings

import android.content.SharedPreferences
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.preference.PreferenceManager
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.ui.DemoChip
import com.verygoodsecurity.demoapp.ui.DemoDropdown
import com.verygoodsecurity.demoapp.ui.DemoField
import com.verygoodsecurity.demoapp.ui.DemoFormCard
import com.verygoodsecurity.demoapp.ui.DemoIconButton
import com.verygoodsecurity.demoapp.ui.DemoIconTile
import com.verygoodsecurity.demoapp.ui.DemoScreen
import com.verygoodsecurity.demoapp.ui.DemoSwitchRow
import com.verygoodsecurity.demoapp.ui.DemoTheme
import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultAliasFormat
import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultStorageType

private val storageTypes = VGSVaultStorageType.entries.map { it.name }
private val allAliasFormats = VGSVaultAliasFormat.entries.map { it.name }
private val numberAliasFormats = allAliasFormats - listOf(
    VGSVaultAliasFormat.FPE_SSN_T_FOUR,
    VGSVaultAliasFormat.ALPHANUMERIC_SSN_T_FOUR,
).map { it.name }.toSet()
private val expiryAliasFormats = numberAliasFormats - listOf(
    VGSVaultAliasFormat.FPE_ACC_NUM_T_FOUR,
    VGSVaultAliasFormat.FPE_ALPHANUMERIC_ACC_NUM_T_FOUR,
).map { it.name }.toSet()

/**
 * Per-field tokenization options. Values are stored in the default shared preferences, which the
 * Views tokenization demo reads as well.
 */
@Composable
fun TokenizationSettingsScreen(onBack: () -> Unit) {
    DemoScreen(stringResource(R.string.tokenization_settings_title), onBack) {
        Text(
            stringResource(R.string.tokenization_settings_hint),
            Modifier.padding(horizontal = DemoTheme.spacing.xs),
            style = DemoTheme.typography.body,
            color = DemoTheme.colors.muted
        )
        SettingsSection(
            icon = R.drawable.ic_baseline_person_24,
            title = R.string.card_holder_name_title,
            enabledKey = R.string.tokenization_card_holder_enabled_key,
            storageKey = R.string.tokenization_card_holder_storage_key,
            aliasFormatKey = R.string.tokenization_card_holder_alias_format_key,
            aliasFormats = allAliasFormats,
            defaultAliasFormat = TokenizationSettings.DEFAULT_CARD_HOLDER_FORMAT,
        )
        SettingsSection(
            icon = R.drawable.ic_baseline_credit_card_24,
            title = R.string.card_number_title,
            aliasFormatKey = R.string.tokenization_card_number_alias_format_key,
            aliasFormats = numberAliasFormats,
            defaultAliasFormat = TokenizationSettings.DEFAULT_CARD_NUMBER_FORMAT,
        )
        SettingsSection(
            icon = R.drawable.ic_baseline_date_range_24,
            title = R.string.expiry_title,
            enabledKey = R.string.tokenization_expiry_enabled_key,
            storageKey = R.string.tokenization_expiry_storage_key,
            aliasFormatKey = R.string.tokenization_expiry_alias_format_key,
            aliasFormats = expiryAliasFormats,
            defaultAliasFormat = TokenizationSettings.DEFAULT_EXPIRY_FORMAT,
        )
    }
}

/** Top bar button that opens [TokenizationSettingsActivity]'s screen; pass it as `DemoScreen(actions = ...)`. */
@Composable
fun SettingsAction(onClick: () -> Unit) {
    DemoIconButton(
        painterResource(R.drawable.ic_baseline_settings_24),
        stringResource(R.string.tokenization_settings_menu_item_title),
        onClick
    )
}

/**
 * One card per field. Fields without an [enabledKey] are always tokenized and have no [storageKey].
 */
@Composable
private fun SettingsSection(
    @DrawableRes icon: Int,
    @StringRes title: Int,
    @StringRes aliasFormatKey: Int,
    aliasFormats: List<String>,
    defaultAliasFormat: VGSVaultAliasFormat,
    @StringRes enabledKey: Int? = null,
    @StringRes storageKey: Int? = null,
) {
    val prefs = rememberPreferences()
    var enabled by rememberPreference(prefs, enabledKey?.let { stringResource(it) }, true)
    var storage by rememberPreference(prefs, storageKey?.let { stringResource(it) }, TokenizationSettings.DEFAULT_STORAGE.name)
    var aliasFormat by rememberPreference(prefs, stringResource(aliasFormatKey), defaultAliasFormat.name)
    val colors = DemoTheme.colors

    DemoFormCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(DemoTheme.spacing.m)) {
            DemoIconTile(painterResource(icon))
            Text(stringResource(title), Modifier.weight(1f), style = DemoTheme.typography.title, color = colors.ink)
            when {
                enabledKey == null -> DemoChip(stringResource(R.string.tokenization_status_always), colors.muted)
                enabled -> DemoChip(stringResource(R.string.tokenization_status_on), colors.success, showDot = true)
                else -> DemoChip(stringResource(R.string.tokenization_status_off), colors.muted)
            }
        }
        if (enabledKey != null) {
            DemoSwitchRow(stringResource(R.string.tokenization_enabled), enabled, { enabled = it })
        }
        if (enabled) {
            if (storageKey != null) {
                DemoField(stringResource(R.string.tokenization_storage)) {
                    DemoDropdown(storageTypes, storage, { storage = it })
                }
            }
            DemoField(stringResource(R.string.tokenization_alias_format)) {
                DemoDropdown(aliasFormats, aliasFormat, { aliasFormat = it })
            }
        }
    }
}

@Composable
private fun rememberPreferences(): SharedPreferences =
    LocalContext.current.let { remember { PreferenceManager.getDefaultSharedPreferences(it) } }

/** State backed by [prefs]: read once, written on every change. A `null` [key] keeps it in memory only. */
@Composable
private fun <T : Any> rememberPreference(prefs: SharedPreferences, key: String?, default: T): MutableState<T> {
    val state = remember(key) { mutableStateOf(prefs.read(key, default)) }
    return remember(state) { PersistedState(state) { prefs.write(key, it) } }
}

private class PersistedState<T>(
    private val state: MutableState<T>,
    private val persist: (T) -> Unit
) : MutableState<T> by state {

    override var value: T
        get() = state.value
        set(value) {
            state.value = value
            persist(value)
        }
}

@Suppress("UNCHECKED_CAST")
private fun <T : Any> SharedPreferences.read(key: String?, default: T): T = when {
    key == null -> default
    default is Boolean -> getBoolean(key, default) as T
    default is String -> (getString(key, default) ?: default) as T
    else -> default
}

private fun <T : Any> SharedPreferences.write(key: String?, value: T) {
    if (key == null) return
    edit().apply {
        when (value) {
            is Boolean -> putBoolean(key, value)
            is String -> putString(key, value)
        }
    }.apply()
}

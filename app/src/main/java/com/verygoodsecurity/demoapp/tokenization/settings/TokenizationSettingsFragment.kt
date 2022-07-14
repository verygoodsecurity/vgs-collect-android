package com.verygoodsecurity.demoapp.tokenization.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.preference.CheckBoxPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.verygoodsecurity.demoapp.R

class TokenizationSettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private var cbpHolderNameEnabled: CheckBoxPreference? = null
    private var ltHolderNameStorage: ListPreference? = null
    private var ltHolderNameAliasFormat: ListPreference? = null
    private var cbpExpiryEnabled: CheckBoxPreference? = null
    private var ltExpiryStorage: ListPreference? = null
    private var ltExpiryAliasFormat: ListPreference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.tokenization_settings)
        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
        initPreferences()
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        if (p0 == null) {
            return
        }
        when (p1) {
            cbpHolderNameEnabled?.key -> setCardHolderPrefsEnabled(p0.getBoolean(p1, true))
            cbpExpiryEnabled?.key -> setExpiryPrefsEnabled(p0.getBoolean(p1, true))
        }
    }

    private fun initPreferences() {
        cbpHolderNameEnabled = findPreference(R.string.tokenization_card_holder_enabled_key)
        ltHolderNameStorage = findPreference(R.string.tokenization_card_holder_storage_key)
        ltHolderNameAliasFormat = findPreference(R.string.tokenization_card_holder_alias_format_key)
        setCardHolderPrefsEnabled(getBoolean(R.string.tokenization_card_holder_enabled_key, true))

        cbpExpiryEnabled = findPreference(R.string.tokenization_expiry_enabled_key)
        ltExpiryStorage = findPreference(R.string.tokenization_expiry_storage_key)
        ltExpiryAliasFormat = findPreference(R.string.tokenization_expiry_alias_format_key)
        setExpiryPrefsEnabled(getBoolean(R.string.tokenization_expiry_enabled_key, true))
    }

    private fun setCardHolderPrefsEnabled(isEnabled: Boolean) {
        ltHolderNameStorage?.isVisible = isEnabled
        ltHolderNameAliasFormat?.isVisible = isEnabled
    }

    private fun setExpiryPrefsEnabled(isEnabled: Boolean) {
        ltExpiryStorage?.isVisible = isEnabled
        ltExpiryAliasFormat?.isVisible = isEnabled
    }

    companion object {

        fun create(): Fragment = TokenizationSettingsFragment()
    }
}

private fun PreferenceFragmentCompat.getBoolean(@StringRes key: Int, defValue: Boolean): Boolean {
    return getBoolean(getString(key), defValue)
}

private fun PreferenceFragmentCompat.getBoolean(key: String, defValue: Boolean): Boolean {
    return preferenceManager?.sharedPreferences?.getBoolean(key, defValue) ?: defValue
}

private fun <T : Preference> PreferenceFragmentCompat.findPreference(@StringRes id: Int): T? {
    return findPreference(getString(id))
}
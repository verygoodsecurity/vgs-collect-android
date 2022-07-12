package com.verygoodsecurity.demoapp.tokenization.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceFragmentCompat
import com.verygoodsecurity.demoapp.R

class TokenizationSettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.tokenization_settings)
        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
        initPreferences()
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {

    }

    private fun initPreferences() {

    }

    companion object {

        fun create(): Fragment = TokenizationSettingsFragment()
    }
}
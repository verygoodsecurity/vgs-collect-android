package com.verygoodsecurity.demoapp.start

import androidx.annotation.StringRes
import com.verygoodsecurity.demoapp.BuildConfig
import com.verygoodsecurity.demoapp.R

enum class StartEnvironment(val value: String, @StringRes val label: Int) {
    SANDBOX("sandbox", R.string.start_activity_environment_sandbox),
    LIVE("live", R.string.start_activity_environment_Live)
}

/** Vault settings entered on the start screen and passed to every flow. */
data class StartConfig(
    val tenantId: String = BuildConfig.TENANT_ID,
    val path: String = BuildConfig.PATH,
    val environment: StartEnvironment = StartEnvironment.SANDBOX
)

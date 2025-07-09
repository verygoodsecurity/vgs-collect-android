package com.verygoodsecurity.vgscollect.core

import android.content.Context
import com.verygoodsecurity.sdk.analytics.VGSSharedAnalyticsManager
import com.verygoodsecurity.sdk.analytics.model.VGSAnalyticsEvent
import com.verygoodsecurity.vgscollect.BuildConfig
import java.util.UUID

internal class AnalyticsHandler(context: Context, vaultId: String?, val environment: String) {

    companion object {

        private const val SOURCE_TAG = "androidSDK"
        private const val DEPENDENCY_MANAGER = "maven"
    }

    private val formId: String = UUID.randomUUID().toString()
    private val analyticId: String = vaultId ?: context.applicationContext.packageName
    private val analyticsManager: VGSSharedAnalyticsManager = VGSSharedAnalyticsManager(
        SOURCE_TAG,
        BuildConfig.VERSION_NAME,
        DEPENDENCY_MANAGER
    )

    fun capture(event: VGSAnalyticsEvent) {
        analyticsManager.capture(
            vault = analyticId,
            environment = environment,
            formId = formId,
            event = event
        )
    }

    fun setIsEnabled(isEnabled: Boolean) {
        analyticsManager.setIsEnabled(isEnabled)
    }

    fun getIsEnabled(): Boolean = analyticsManager.getIsEnabled()

    fun cancelAll() {
        analyticsManager.cancelAll()
    }
}
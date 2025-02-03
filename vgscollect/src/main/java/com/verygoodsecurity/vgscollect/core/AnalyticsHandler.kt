package com.verygoodsecurity.vgscollect.core

import com.verygoodsecurity.sdk.analytics.model.VGSAnalyticsEvent

internal interface AnalyticsHandler {

    fun capture(event: VGSAnalyticsEvent)
}
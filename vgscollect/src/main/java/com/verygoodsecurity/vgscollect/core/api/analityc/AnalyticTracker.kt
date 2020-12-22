package com.verygoodsecurity.vgscollect.core.api.analityc

import com.verygoodsecurity.vgscollect.core.api.analityc.action.Action

interface AnalyticTracker {
    fun setAnalyticsEnabled(isEnabled: Boolean)
    fun logEvent(action: Action)
}
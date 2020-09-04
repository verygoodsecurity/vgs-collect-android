package com.verygoodsecurity.vgscollect.core.api.analityc

import com.verygoodsecurity.vgscollect.core.api.analityc.action.Action

internal interface AnalyticTracker {
    fun logEvent(action: Action)
}
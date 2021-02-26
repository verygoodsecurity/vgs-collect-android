package com.verygoodsecurity.vgscollect.core.api.analityc

import com.verygoodsecurity.vgscollect.core.api.analityc.action.Action

interface AnalyticTracker {
    var isEnabled: Boolean
    fun logEvent(action: Action)
}
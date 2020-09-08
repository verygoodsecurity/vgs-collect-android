package com.verygoodsecurity.vgscollect.core.api.analityc

import com.verygoodsecurity.vgscollect.core.api.analityc.action.Action

interface AnalyticTracker {
    fun logEvent(action: Action)
}
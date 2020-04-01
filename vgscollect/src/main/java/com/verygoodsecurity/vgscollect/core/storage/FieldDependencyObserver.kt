package com.verygoodsecurity.vgscollect.core.storage

import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState

/** @suppress */
internal interface FieldDependencyObserver {
    fun onRefreshState(state: VGSFieldState)
    fun onStateUpdate(state: VGSFieldState)
}
package com.verygoodsecurity.vgscollect.core.storage

import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState

interface VgsStore {
    fun clear()
    fun addItem(viewId: Int, newState: VGSFieldState)
    fun getStates(): MutableCollection<VGSFieldState>
}
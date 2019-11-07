package com.verygoodsecurity.vgscollect.core

import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState

internal interface OnVgsViewStateChangeListener {
    fun emit(viewId:Int, state: VGSFieldState)
}
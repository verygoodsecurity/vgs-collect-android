package com.verygoodsecurity.vgscollect.core

import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState

interface OnVgsViewStateChangeListener {
    fun emit(viewId:Int, state: VGSFieldState)
}
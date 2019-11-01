package com.verygoodsecurity.vgscollect.core

import com.verygoodsecurity.vgscollect.view.VGSFieldState

internal interface OnVgsViewStateChangeListener {
    fun emit(viewId:Int, state: VGSFieldState)
}
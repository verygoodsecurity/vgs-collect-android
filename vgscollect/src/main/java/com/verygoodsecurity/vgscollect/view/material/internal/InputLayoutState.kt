package com.verygoodsecurity.vgscollect.view.material.internal

import com.verygoodsecurity.vgscollect.view.FieldState

/** @suppress */
internal interface InputLayoutState:FieldState {
    fun restore(textInputLayoutWrapper: TextInputLayoutWrapper?)
}
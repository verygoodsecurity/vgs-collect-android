package com.verygoodsecurity.vgscollect.view

import android.view.View

interface FieldState {
    fun refresh()
    fun isReady():Boolean
}
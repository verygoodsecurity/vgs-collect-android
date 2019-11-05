package com.verygoodsecurity.vgscollect.core.storage

import com.verygoodsecurity.vgscollect.core.model.state.FieldState

interface OnFieldStateChangeListener {
    fun onStateChange(state:FieldState)

    fun onStateChange(states:Collection<FieldState>)    //todo remove before release
}
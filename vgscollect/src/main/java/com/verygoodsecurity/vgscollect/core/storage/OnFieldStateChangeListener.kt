package com.verygoodsecurity.vgscollect.core.storage

import com.verygoodsecurity.vgscollect.core.model.state.FieldState

/**
 * A listener for receiving notifications about VGS input field state changes.
 */
interface OnFieldStateChangeListener {

    /**
     * Called when a VGS input field's state has changed.
     *
     * @param state The new state of the input field.
     */
    fun onStateChange(state:FieldState)
}
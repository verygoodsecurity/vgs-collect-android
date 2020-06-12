package com.verygoodsecurity.vgscollect.core.storage

import com.verygoodsecurity.vgscollect.core.model.state.FieldState


/**
 * Interface definition for a callback to be invoked when a view state is changed.
 *
 * @version 1.0.0
 */
interface OnFieldStateChangeListener {

    /**
     * Called when new changes is detected
     *
     * @param state current state of input field
     */
    fun onStateChange(state:FieldState)
}
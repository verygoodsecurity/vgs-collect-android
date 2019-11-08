package com.verygoodsecurity.vgscollect.core.storage

import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.core.model.state.mapToFieldState

internal class DefaultStorage {

    private val store = mutableMapOf<Int, VGSFieldState>()

    var onFieldStateChangeListener: OnFieldStateChangeListener? = null
        @JvmName("attachStateChangeListener") set

    fun clear() {
        store.clear()
    }

    fun getStates() = store.values

    fun performSubscription() = object: OnVgsViewStateChangeListener {
        override fun emit(viewId: Int, state: VGSFieldState) {
            addItem(viewId, state)
        }
    }

    fun addItem(viewId: Int, newState: VGSFieldState) {
        store[viewId] = newState
        notifyUser(newState)
    }

    private fun notifyUser(state: VGSFieldState) {
        val fs = state.mapToFieldState()
        onFieldStateChangeListener?.onStateChange(fs)
    }
}

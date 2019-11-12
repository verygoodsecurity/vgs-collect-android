package com.verygoodsecurity.vgscollect.core.storage

import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.core.model.state.mapToFieldState

internal class DefaultStorage:VgsStore,IStateEmitter {

    private val store = mutableMapOf<Int, VGSFieldState>()

    private var onFieldStateChangeListener: OnFieldStateChangeListener? = null

    override fun attachStateChangeListener(listener: OnFieldStateChangeListener?) {
        onFieldStateChangeListener = listener
    }

    override fun clear() {
        store.clear()
    }

    override fun getStates() = store.values

    override fun performSubscription() = object: OnVgsViewStateChangeListener {
        override fun emit(viewId: Int, state: VGSFieldState) {
            addItem(viewId, state)
        }
    }

    override fun addItem(viewId: Int, newState: VGSFieldState) {
        store[viewId] = newState
        notifyUser(newState)
    }

    private fun notifyUser(state: VGSFieldState) {
        val fs = state.mapToFieldState()
        onFieldStateChangeListener?.onStateChange(fs)
    }
}

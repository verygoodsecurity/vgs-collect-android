package com.verygoodsecurity.vgscollect.core.storage

import android.util.Log
import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.model.VGSFieldState
import com.verygoodsecurity.vgscollect.core.model.mapToFieldState
import com.verygoodsecurity.vgscollect.core.model.state.FieldState

internal class DefaultStorage {

    private val store = mutableMapOf<Int, VGSFieldState>()
    var onFieldStateChangeListener: OnFieldStateChangeListener? = null



    fun clear() {
        store.clear()
    }

    fun getStates() = store.values

    fun performSubscription() = object: OnVgsViewStateChangeListener {
        override fun emit(viewId: Int, state: VGSFieldState) {
            store[viewId] = state
            sendToUser(state)
            Log.e("DefaultStorage ${store.size}", "$viewId ${state.alias} ${state.type.name} ${state.content} ${state.isFocusable} ${state.isRequired}")
        }
    }

    private fun sendToUser(state: VGSFieldState) {
        onFieldStateChangeListener?.onStateChange(state.mapToFieldState())

        val c = store.values.map { it.mapToFieldState() }
        onFieldStateChangeListener?.onStateChange(c)
    }
}

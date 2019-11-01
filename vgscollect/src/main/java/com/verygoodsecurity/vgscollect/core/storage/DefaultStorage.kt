package com.verygoodsecurity.vgscollect.core.storage

import android.util.Log
import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.model.VGSFieldState

internal class DefaultStorage {

    private val store = mutableMapOf<Int, VGSFieldState>()

    fun clear() {
        store.clear()
    }

    fun getStates() = store.values

    fun performSubscription() = object: OnVgsViewStateChangeListener {
        override fun emit(viewId: Int, state: VGSFieldState) {
            store[viewId] = state
            Log.e("test", "$viewId ${state.alias} ${state.type.name} ${state.placeholder} ${state.content} ${state.isFocusable}")
        }
    }
}

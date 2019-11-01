package com.verygoodsecurity.vgscollect.core.storage

import android.util.Log
import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.view.EditTextWrapper
import com.verygoodsecurity.vgscollect.view.VGSFieldState

internal class DefaultStorage {

    private val store = mutableMapOf<Int, VGSFieldState>()

    fun clear() {
        store.clear()
    }

    private val store2 = mutableListOf<EditTextWrapper>()
    fun getConfigurations(): Map<String, String> {
        store.forEach {
            Log.e("test", "${it.key} : ${it.value.alias}")
        }
        return store2.map { it.getVGSInputType().name to it.getTextString() }.toMap()
    }

    fun performSubscription() = object: OnVgsViewStateChangeListener {
        override fun emit(viewId: Int, state: VGSFieldState) {
            store[viewId] = state
        }
    }
}
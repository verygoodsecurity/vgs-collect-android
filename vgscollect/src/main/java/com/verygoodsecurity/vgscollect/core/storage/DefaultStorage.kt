package com.verygoodsecurity.vgscollect.core.storage

import com.verygoodsecurity.vgscollect.view.EditTextWrapper
import com.verygoodsecurity.vgscollect.widget.VGSEditText

internal class DefaultStorage {
    val store = mutableListOf<EditTextWrapper>()

    fun putView(view: VGSEditText) {
        store.add(view.inputField)
    }

    fun clear() {
        store.clear()
    }

    fun getConfigurations(): Map<String, String> {
        return store.map { it.getVGSInputType().name to it.getTextString() }.toMap()
    }
}
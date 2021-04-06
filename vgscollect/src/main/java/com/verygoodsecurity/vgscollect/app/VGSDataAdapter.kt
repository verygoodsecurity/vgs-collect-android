package com.verygoodsecurity.vgscollect.app

import android.content.Intent
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.model.VGSHashMapWrapper
import com.verygoodsecurity.vgscollect.view.card.FieldType

abstract class VGSDataAdapter {
    internal lateinit var vgsCollect: VGSCollect

    internal fun attachSource(fieldName: String?, fieldType: FieldType?) {
        if(!fieldName.isNullOrEmpty() && fieldType != null) {
            fields[fieldName] = fieldType
        }
    }

    internal fun detachSource(fieldName: String?) {
        fieldName?.let {
            fields.remove(it)
        }
    }

    abstract fun navigateUpTo(i: Intent?)
    abstract fun initialize()

    protected fun send() {
        vgsCollect.dispatchData(storage)
    }

    protected fun mapData(key: String?, value: Any?) {
        if (!key.isNullOrEmpty() && value != null) {
            storage.put(key, value)
        }
    }

    private val storage = VGSHashMapWrapper<String, Any?>()

    protected val fields = mutableMapOf<String, FieldType>()

}
package com.verygoodsecurity.vgscollect.core.storage.external

import com.verygoodsecurity.vgscollect.core.storage.DependencyListener

internal interface ExternalDependencyDispatcher {
    fun addDependencyListener(fieldName: String?, notifier: DependencyListener)
    fun dispatch(map: HashMap<String, Any?>)
    fun dispatch(fieldName:String, value:Any)
}
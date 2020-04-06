package com.verygoodsecurity.vgscollect.core.storage.external

import com.verygoodsecurity.vgscollect.core.storage.DependencyListener
import com.verygoodsecurity.vgscollect.view.InputFieldView
import java.util.HashMap

internal interface ExternalDependencyDispatcher {
    fun addDependencyListener(fieldName: String?, notifier: DependencyListener)
    fun dispatch(map: HashMap<String, Any?>)
    fun dispatch(fieldName:String, value:Any)
}
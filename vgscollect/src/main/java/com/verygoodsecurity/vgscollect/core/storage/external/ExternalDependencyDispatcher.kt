package com.verygoodsecurity.vgscollect.core.storage.external

import com.verygoodsecurity.vgscollect.view.InputFieldView
import java.util.HashMap

interface ExternalDependencyDispatcher {
    fun addDependencyListener(fieldName: String?, notifier: InputFieldView.DependencyNotifier)
    fun dispatch(map: HashMap<String, Any?>)
    fun dispatch(fieldName:String, value:Any)
}
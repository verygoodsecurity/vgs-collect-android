package com.verygoodsecurity.vgscollect.core.storage.external

import com.verygoodsecurity.vgscollect.core.model.state.Dependency
import com.verygoodsecurity.vgscollect.core.storage.DependencyListener
import com.verygoodsecurity.vgscollect.core.storage.DependencyType
import java.util.HashMap

class DependencyReceiver:ExternalDependencyDispatcher {
    private val views = mutableMapOf<String, DependencyListener>()

    override fun addDependencyListener(
        fieldName: String?,
        notifier: DependencyListener
    ) {
        fieldName?.run {
            views[this] = notifier
        }
    }

    override fun dispatch(map: HashMap<String, Any?>) {
        map.forEach {
            if(it.value != null) {
                val d = Dependency(DependencyType.TEXT, it.value!!)
                views[it.key]?.dispatchDependencySetting(d)
            }
        }
    }

    override fun dispatch(fieldName: String, value: Any) {
        views[fieldName]?.dispatchDependencySetting(Dependency(DependencyType.TEXT, value))
    }
}
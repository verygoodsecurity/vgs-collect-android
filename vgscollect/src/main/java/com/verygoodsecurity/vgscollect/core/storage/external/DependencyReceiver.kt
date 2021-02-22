package com.verygoodsecurity.vgscollect.core.storage.external

import com.verygoodsecurity.vgscollect.core.model.state.Dependency
import com.verygoodsecurity.vgscollect.core.storage.DependencyListener
import java.util.HashMap

internal class DependencyReceiver:ExternalDependencyDispatcher {
    private val views = mutableMapOf<String, DependencyListener>()

    override fun addDependencyListener(
        fieldName: String?,
        notifier: DependencyListener
    ) {
        fieldName?.let {
            views[it] = notifier
        }
    }

    override fun dispatch(map: HashMap<String, Any?>) {
        map.forEach {
            if(it.value != null) {
                val d = Dependency.text(it.value!!)
                views[it.key]?.dispatchDependencySetting(d)
            }
        }
    }

    override fun dispatch(fieldName: String, value: Any) {
        views[fieldName]?.dispatchDependencySetting(Dependency.text(value))
    }
}
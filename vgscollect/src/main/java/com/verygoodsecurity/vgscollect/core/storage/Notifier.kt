package com.verygoodsecurity.vgscollect.core.storage

import com.verygoodsecurity.vgscollect.core.model.state.Dependency
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.card.FieldType

class Notifier:
    DependencyDispatcher {
    private val views = mutableMapOf<FieldType, DependencyListener>()

    override fun addDependencyListener(fieldType: FieldType, notifier: InputFieldView.DependencyNotifier) {
        views[fieldType] = notifier
    }

    override fun onDependencyDetected(type: FieldType, dependency: Dependency) {
        views.forEach {
            if(type == it.key) {
                it.value.dispatchDependencySetting(dependency)
            }
        }
    }
}
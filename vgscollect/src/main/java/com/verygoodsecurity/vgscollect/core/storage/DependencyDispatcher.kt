package com.verygoodsecurity.vgscollect.core.storage

import com.verygoodsecurity.vgscollect.core.model.state.Dependency
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.card.FieldType

internal interface DependencyDispatcher {
    fun onDependencyDetected(type: FieldType, dependency: Dependency)
    fun addDependencyListener(fieldType: FieldType, notifier: DependencyListener)
}
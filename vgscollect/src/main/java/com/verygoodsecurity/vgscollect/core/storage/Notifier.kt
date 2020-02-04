package com.verygoodsecurity.vgscollect.core.storage

import com.verygoodsecurity.vgscollect.core.model.state.*
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.card.FieldType

class Notifier: DependencyDispatcher, FieldDependencyObserver {

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

    override fun onStateUpdate(state: VGSFieldState) {
        notifyRelatedFields(state, ::onDependencyDetected)
    }

    override fun onRefreshState(state: VGSFieldState) {
        notifyRelatedFields(state, ::onDependencyDetected)
    }

    private fun notifyRelatedFields(state: VGSFieldState, onDependencyDetected: (FieldType, Dependency) -> Unit) {
        val maxCvcLength = (state.content as? FieldContent.CardNumberContent)?.CVCMaxLength()?:4
        val dependency = Dependency(DependencyType.LENGTH, maxCvcLength)

        when {
            state.isCardNumberType() -> onDependencyDetected(FieldType.CVC, dependency)
        }
    }
}
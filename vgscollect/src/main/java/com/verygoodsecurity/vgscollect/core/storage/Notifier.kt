package com.verygoodsecurity.vgscollect.core.storage

import com.verygoodsecurity.vgscollect.core.model.state.*
import com.verygoodsecurity.vgscollect.view.card.FieldType

typealias DependencyHandler = (FieldType, Dependency) -> Unit

/** @suppress */
internal class Notifier: DependencyDispatcher, FieldDependencyObserver {

    private val views = mutableMapOf<FieldType, DependencyListener>()

    override fun addDependencyListener(fieldType: FieldType, notifier: DependencyListener) {
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

    private fun notifyRelatedFields(state: VGSFieldState, onDependencyDetected: DependencyHandler) {
        when {
            state.isCardNumberType() -> {
                val r:Array<Int> = (state.content as? FieldContent.CardNumberContent)?.rangeCVV?.sortedArray()?: arrayOf(3,4)
                val dependency = Dependency(DependencyType.RANGE, r)
                onDependencyDetected(FieldType.CVC, dependency)
            }
        }
    }
}
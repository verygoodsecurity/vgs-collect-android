package com.verygoodsecurity.vgscollect.core.storage

import com.verygoodsecurity.vgscollect.core.model.state.*
import com.verygoodsecurity.vgscollect.view.card.FieldType

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

    private fun notifyRelatedFields(state: VGSFieldState, onDependencyDetected: (FieldType, Dependency) -> Unit) {
        val range = Pair(getMinCVCLength(state), getMaxCVCLength(state))
        val dependency = Dependency(DependencyType.RANGE, range)

        when {
            state.isCardNumberType() -> onDependencyDetected(FieldType.CVC, dependency)
        }
    }

    private fun getMinCVCLength(state: VGSFieldState):Int {
        return (state.content as? FieldContent.CardNumberContent)?.CVCMinLength()?:3
    }

    private fun getMaxCVCLength(state: VGSFieldState):Int {
        return (state.content as? FieldContent.CardNumberContent)?.CVCMaxLength()?:4
    }
}
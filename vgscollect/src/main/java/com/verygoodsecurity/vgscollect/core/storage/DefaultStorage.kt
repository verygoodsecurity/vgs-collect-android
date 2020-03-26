package com.verygoodsecurity.vgscollect.core.storage

import androidx.annotation.VisibleForTesting
import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.model.state.*
import com.verygoodsecurity.vgscollect.view.card.FieldType

internal class DefaultStorage : VgsStore<Int, VGSFieldState>,IStateEmitter {

    private val store = mutableMapOf<Int, VGSFieldState>()

    private val dependencyObservers = mutableListOf<FieldDependencyObserver>()
    private val onFieldStateChangeListeners = mutableListOf<OnFieldStateChangeListener>()

    override fun attachStateChangeListener(listener: OnFieldStateChangeListener?) {
        listener?.let { onFieldStateChangeListeners.add(it) }
    }

    override fun attachFieldDependencyObserver(listener: FieldDependencyObserver?) {
        listener?.let { dependencyObservers.add(it) }
    }

    override fun clear() {
        store.clear()
    }

    override fun getItems() = store.values

    override fun performSubscription() = object: OnVgsViewStateChangeListener {
        override fun emit(viewId: Int, state: VGSFieldState) {
            if(store.containsKey(viewId).not()) {
                notifyOnNewFieldAdd(state)
            }
            addItem(viewId, state)
            notifyOnDataChange(state)
        }
    }

    private fun notifyOnNewFieldAdd(state: VGSFieldState) {
        if(state.isCardCVCType()) {
            store.forEach {
                if(it.value.type == FieldType.CARD_NUMBER) {
                    refreshDependenciesOnNewField(it.value)
                }
            }
        }
    }

    private fun notifyOnDataChange(state: VGSFieldState) {
        if(state.isCardNumberType()) {
            refreshDependencies(state)
        }
    }

    private fun refreshDependencies(value: VGSFieldState) {
        dependencyObservers.forEach { it.onStateUpdate(value) }
    }

    private fun refreshDependenciesOnNewField(value: VGSFieldState) {
        dependencyObservers.forEach { it.onRefreshState(value) }
    }

    override fun addItem(viewId: Int, newState: VGSFieldState) {
        store[viewId] = newState
        notifyUser(newState)
    }

    private fun notifyUser(state: VGSFieldState) {
        val fs = state.mapToFieldState()
        onFieldStateChangeListeners.forEach { it.onStateChange(fs) }
    }

    @VisibleForTesting
    fun  getFieldStateChangeListeners(): MutableList<OnFieldStateChangeListener> {
        return onFieldStateChangeListeners
    }

    @VisibleForTesting
    fun  getDependencyObservers(): MutableList<FieldDependencyObserver> {
        return dependencyObservers
    }
}

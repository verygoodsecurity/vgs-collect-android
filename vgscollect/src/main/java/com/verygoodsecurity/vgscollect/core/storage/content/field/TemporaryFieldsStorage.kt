package com.verygoodsecurity.vgscollect.core.storage.content.field

import androidx.annotation.VisibleForTesting
import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.model.state.*
import com.verygoodsecurity.vgscollect.core.storage.*
import com.verygoodsecurity.vgscollect.core.storage.FieldDependencyObserver
import com.verygoodsecurity.vgscollect.core.storage.IStateEmitter
import com.verygoodsecurity.vgscollect.core.storage.VgsStore
import com.verygoodsecurity.vgscollect.view.card.FieldType

internal class TemporaryFieldsStorage(
    private val contractor: StorageContractor<VGSFieldState>
) : VgsStore<Int, VGSFieldState>, IStateEmitter {

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

    override fun remove(id: Int) {
        store.remove(id)
    }

    override fun getItems(): MutableCollection<VGSFieldState> {
        return store.values
    }

    private fun Map<Int, VGSFieldState>.containsState(state: VGSFieldState):Int {
        forEach {
            if(state.fieldName == it.value.fieldName) {
                return it.key
            }
        }

        return -1
    }

    override fun performSubscription() = object: OnVgsViewStateChangeListener {
        override fun emit(viewId: Int, state: VGSFieldState) {
            if (contractor.checkState(state)) {
                addItem(viewId, state)
            }
        }
    }

    override fun addItem(viewId: Int, newState: VGSFieldState) {
        processNewState(viewId, newState)
    }

    private fun processNewState(newViewId: Int, state: VGSFieldState) {
        val previousStateId = store.containsState(state)

        if (previousStateId == -1) {
            notifyOnNewFieldAdd(state)
        } else {
            val previousInteractionState = store[newViewId]?.hasUserInteraction ?: false
            state.hasUserInteraction = state.hasUserInteraction || previousInteractionState

            if (newViewId != previousStateId) {
                store.remove(previousStateId)
            }

            notifyUserOnDataChange(state)
        }

        store[newViewId] = state

        notifyDependentFieldsOnDataChange(state)
    }

    private fun notifyOnNewFieldAdd(state: VGSFieldState) {
        if(state.isCardCVCType()) {
            store.forEach {
                if(it.value.type == FieldType.CARD_NUMBER) {
                    refreshDependenciesOnNewField(it.value)
                    return
                }
            }
        }
    }

    private fun notifyDependentFieldsOnDataChange(state: VGSFieldState) {
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

    private fun notifyUserOnDataChange(state: VGSFieldState) {
        if (state.hasUserInteraction) {
            val fs = state.mapToFieldState()
            onFieldStateChangeListeners.forEach { it.onStateChange(fs) }
        }
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

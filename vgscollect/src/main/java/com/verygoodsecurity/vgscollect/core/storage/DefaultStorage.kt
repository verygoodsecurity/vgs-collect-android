package com.verygoodsecurity.vgscollect.core.storage

import com.verygoodsecurity.vgscollect.core.model.state.Dependency
import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.core.model.state.isCardNumberType
import com.verygoodsecurity.vgscollect.core.model.state.mapToFieldState
import com.verygoodsecurity.vgscollect.view.card.FieldType

internal class DefaultStorage(
    private val notifier: DependencyDispatcher? = null
):VgsStore,IStateEmitter {

    private val store = mutableMapOf<Int, VGSFieldState>()

    private var onFieldStateChangeListener: OnFieldStateChangeListener? = null

    override fun attachStateChangeListener(listener: OnFieldStateChangeListener?) {
        onFieldStateChangeListener = listener
    }

    override fun clear() {
        store.clear()
    }

    override fun getStates() = store.values

    override fun performSubscription() = object: OnVgsViewStateChangeListener {
        override fun emit(viewId: Int, state: VGSFieldState) {
            notifyRelatedFields(state)
            addItem(viewId, state)
        }
    }

    private fun notifyRelatedFields(state: VGSFieldState) {
        if(state.isCardNumberType()) {
            val maxCvcLength = (state.content as? FieldContent.CardNumberContent)?.cardtype?.rangeCVV?.last()?:4
            val dependency =
                Dependency(
                    DependencyType.LENGTH,
                    maxCvcLength
                )
            notifier?.onDependencyDetected(FieldType.CVC, dependency)
        }
    }

    override fun addItem(viewId: Int, newState: VGSFieldState) {
        store[viewId] = newState
        notifyUser(newState)
    }

    private fun notifyUser(state: VGSFieldState) {
        val fs = state.mapToFieldState()
        onFieldStateChangeListener?.onStateChange(fs)
    }
}

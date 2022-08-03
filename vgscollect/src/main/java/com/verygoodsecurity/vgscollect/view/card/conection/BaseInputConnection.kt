package com.verygoodsecurity.vgscollect.view.card.conection

import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.view.card.validation.CompositeValidator

internal abstract class BaseInputConnection constructor(
    private val id: Int,
    private val validator: CompositeValidator
) : InputRunnable {

    protected var state = VGSFieldState()

    override fun setOutput(state: VGSFieldState) {
        this.state = state
    }

    override fun getOutput() = state

    private var stateListeners = mutableListOf<OnVgsViewStateChangeListener>()

    protected fun isValid(input: String): Boolean = validator.isValid(input)

    protected fun clearAllListeners() {
        stateListeners.clear()
    }

    protected fun addNewListener(listener: OnVgsViewStateChangeListener) {
        if (!stateListeners.contains(listener)) {
            stateListeners.add(listener)
            run()
        }
    }

    protected fun notifyAllListeners(output: VGSFieldState) {
        stateListeners.forEach { it.emit(id, output) }
    }
}
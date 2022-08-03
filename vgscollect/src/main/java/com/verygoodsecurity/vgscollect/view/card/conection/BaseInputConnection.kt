package com.verygoodsecurity.vgscollect.view.card.conection

import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.view.card.validation.CompositeValidator

/** @suppress */
internal abstract class BaseInputConnection constructor(
    private val id: Int,
    protected val validator: CompositeValidator
) : InputRunnable {

    private var stateListeners = mutableListOf<OnVgsViewStateChangeListener>()

    protected var state = VGSFieldState()

    abstract fun getRawContent(content: String?): String

    override fun getOutput() = state

    override fun setOutput(state: VGSFieldState) {
        this.state = state
    }

    override fun setOutputListener(listener: OnVgsViewStateChangeListener?) {
        listener?.let { addNewListener(it) } ?: clearAllListeners()
    }

    override fun run() {
        val errors = validate()
        state.isValid = errors.isEmpty()
        state.errors = errors
        notifyAllListeners(state)
    }

    private fun validate(): List<String> {
        val content = state.content?.data
        return when {
            !state.isRequired && content.isNullOrEmpty() -> emptyList()
            state.enableValidation -> validator.validate(getRawContent(content))
            else -> emptyList()
        }
    }

    private fun clearAllListeners() {
        stateListeners.clear()
    }

    private fun addNewListener(listener: OnVgsViewStateChangeListener) {
        if (!stateListeners.contains(listener)) {
            stateListeners.add(listener)
            run()
        }
    }

    protected fun notifyAllListeners(output: VGSFieldState) {
        stateListeners.forEach { it.emit(id, output) }
    }
}
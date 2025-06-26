package com.verygoodsecurity.vgscollect.view.card.conection

import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.view.card.validation.CompositeValidator
import java.util.concurrent.CopyOnWriteArrayList

/** @suppress */
internal abstract class BaseInputConnection constructor(
    private val id: Int,
    protected val validator: CompositeValidator
) : InputRunnable {

    private var stateListeners = CopyOnWriteArrayList<OnVgsViewStateChangeListener>()

    protected var state = VGSFieldState()

    abstract fun getRawContent(content: String?): String

    override fun getOutput() = state

    override fun setOutput(state: VGSFieldState) {
        this.state = state
    }

    override fun addOutputListener(listener: OnVgsViewStateChangeListener?) {
        listener?.let {
            if (!stateListeners.contains(listener)) {
                stateListeners.add(listener)
                run()
            }
        }
    }

    override fun removeOutputListener(listener: OnVgsViewStateChangeListener?) {
        listener?.let {
            stateListeners.remove(it)
        }
    }

    override fun run() {
        val errors = validate()
        state.isValid = errors.isEmpty()
        state.validationErrors = errors
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

    protected fun notifyAllListeners(output: VGSFieldState) {
        stateListeners.forEach { it.emit(id, output) }
    }
}
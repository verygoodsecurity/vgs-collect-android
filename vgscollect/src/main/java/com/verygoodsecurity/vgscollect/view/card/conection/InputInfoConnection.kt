package com.verygoodsecurity.vgscollect.view.card.conection

import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.view.card.filter.VGSCardFilter
import com.verygoodsecurity.vgscollect.view.card.validation.CompositeValidator

/** @suppress */
internal class InputInfoConnection(
    id: Int,
    validator: CompositeValidator
) : BaseInputConnection(id, validator) {

    override fun setOutputListener(listener: OnVgsViewStateChangeListener?) {
        listener?.let { addNewListener(it) } ?: clearAllListeners()
    }

    override fun run() {
        state.isValid = isRequiredValid() && isContentValid()

        notifyAllListeners(state)
    }

    private fun isContentValid(): Boolean {
        val content = state.content?.data
        return when {
            !state.isRequired && content.isNullOrEmpty() -> true
            state.enableValidation -> checkIsContentValid(content)
            else -> true
        }
    }

    private fun checkIsContentValid(content: String?): Boolean = isValid(content ?: "")

    private fun isRequiredValid(): Boolean {
        return state.isRequired && !state.content?.data.isNullOrEmpty() || !state.isRequired
    }

    override fun clearFilters() {}
    override fun addFilter(filter: VGSCardFilter?) {}

}
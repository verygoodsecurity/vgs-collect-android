package com.verygoodsecurity.vgscollect.view.card.conection

import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.view.card.filter.VGSCardFilter
import com.verygoodsecurity.vgscollect.view.card.validation.VGSValidator

/** @suppress */
internal class InputCardCVCConnection(
    private val id: Int,
    validator: VGSValidator?
) : BaseInputConnection() {
    internal var runtimeValidator: VGSValidator? = validator

    private var output = VGSFieldState()

    override fun setOutput(state: VGSFieldState) {
        output = state
    }

    override fun getOutput() = output

    override fun setOutputListener(l: OnVgsViewStateChangeListener?) {
        l?.let { addNewListener(it) } ?: clearAllListeners()
    }

    override fun run() {
        output.isValid = isRequiredValid() && isContentValid()

        notifyAllListeners(id, output)
    }

    private fun isContentValid(): Boolean {
        val content = output.content?.data
        return when {
            !output.isRequired && content.isNullOrEmpty() -> true
            output.enableValidation -> checkIsContentValid(content)
            else -> true
        }
    }

    private fun checkIsContentValid(content: String?): Boolean {
        val updatedStr = content?.trim() ?: ""

        return runtimeValidator?.isValid(updatedStr) ?: false
    }

    private fun isRequiredValid(): Boolean {
        return output.isRequired && !output.content?.data.isNullOrEmpty() || !output.isRequired
    }

    override fun clearFilters() {}
    override fun addFilter(filter: VGSCardFilter?) {}
}
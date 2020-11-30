package com.verygoodsecurity.vgscollect.view.card.conection

import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.view.card.filter.VGSCardFilter
import com.verygoodsecurity.vgscollect.view.card.validation.VGSValidator

internal class InputSSNConnection(
    private val id:Int,
    private val validator: VGSValidator?
): BaseInputConnection() {
    private var output = VGSFieldState()

    override fun setOutput(state: VGSFieldState) {
        output = state
    }

    override fun getOutput() = output

    override fun setOutputListener(l: OnVgsViewStateChangeListener?) {
        l?.let { addNewListener(it) }?:clearAllListeners()
    }

    override fun run() {
        validate()

        notifyAllListeners(id, output)
    }

    private fun validate() {
        val isRequiredRuleValid = isRequiredValid()
        val isContentRuleValid = isContentValid()

        output.isValid = isRequiredRuleValid && isContentRuleValid
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
        val updatedStr = content?.trim()?:""

        return validator?.isValid(updatedStr)?:false
    }

    private fun isRequiredValid(): Boolean {
        return output.isRequired && !output.content?.data.isNullOrEmpty() || !output.isRequired
    }

    override fun clearFilters() {}
    override fun addFilter(filter: VGSCardFilter?) {}

}
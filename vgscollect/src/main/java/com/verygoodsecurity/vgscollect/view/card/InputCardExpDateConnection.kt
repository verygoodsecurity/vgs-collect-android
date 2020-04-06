package com.verygoodsecurity.vgscollect.view.card

import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.view.card.filter.VGSCardFilter
import com.verygoodsecurity.vgscollect.view.card.validation.VGSValidator

/** @suppress */
internal class InputCardExpDateConnection(
    private val id:Int,
    private vararg val validators: VGSValidator
): InputRunnable {
    private var stateListener: OnVgsViewStateChangeListener? = null

    private var output = VGSFieldState()

    override fun setOutput(state: VGSFieldState) {
        output = state
    }

    override fun getOutput() = output

    override fun setOutputListener(l: OnVgsViewStateChangeListener?) {
        stateListener = l
        run()
    }

    override fun run() {
        val str = output.content?.data
        if(str.isNullOrEmpty() && !output.isRequired) {
            output.isValid = true
        } else {
            val updatedStr = str?.trim()?:""

            var isDateValid = true
            validators.forEach {
                if(isDateValid) {
                    isDateValid = it.isValid(updatedStr)
                }
            }
            output.isValid = isDateValid
        }

        stateListener?.emit(id, output)
    }

    override fun clearFilters() {}
    override fun addFilter(filter: VGSCardFilter?) {}
}
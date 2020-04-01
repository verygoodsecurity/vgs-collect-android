package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import android.text.InputType
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.InputInfoConnection
import com.verygoodsecurity.vgscollect.view.card.validation.InfoValidator

/** @suppress */
internal class InfoInputField(context: Context): BaseInputField(context) {

    override var fieldType: FieldType = FieldType.INFO

    override fun applyFieldType() {
        val validator = InfoValidator()
        inputConnection = InputInfoConnection(id, validator)

        val str = text.toString()
        val stateContent = FieldContent.InfoContent().apply {
            this.data = str
        }
        val state = collectCurrentState(stateContent)

        inputConnection?.setOutput(state)
        inputConnection?.setOutputListener(stateListener)

        applyNewTextWatcher(null)
        filters = arrayOf()
        applyInputType()
    }

    private fun applyInputType() {
        val type = inputType
        if(type == InputType.TYPE_TEXT_VARIATION_PASSWORD || type == InputType.TYPE_NUMBER_VARIATION_PASSWORD) {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        } else {
            inputType = InputType.TYPE_CLASS_TEXT
        }
        refreshInput()
    }

}
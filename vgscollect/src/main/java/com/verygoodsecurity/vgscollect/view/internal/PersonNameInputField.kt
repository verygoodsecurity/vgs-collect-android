package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import android.text.InputFilter
import android.text.InputType
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.InputCardHolderConnection
import com.verygoodsecurity.vgscollect.view.card.validation.CardHolderValidator

internal class PersonNameInputField(context: Context): BaseInputField(context) {

    override var fieldType: FieldType = FieldType.CARD_HOLDER_NAME

    override fun applyFieldType() {
        validator = CardHolderValidator()
        inputConnection = InputCardHolderConnection(id, validator)

        val str = text.toString()
        val stateContent = FieldContent.InfoContent().apply {
            this.data = str
        }
        val state = collectCurrentState(stateContent)

        inputConnection?.setOutput(state)
        inputConnection?.setOutputListener(stateListener)

        applyNewTextWatcher(null)
        val filterLength = InputFilter.LengthFilter(256)
        filters = arrayOf(filterLength)
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
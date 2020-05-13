package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import android.text.InputFilter
import android.text.InputType
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.InputCardHolderConnection
import com.verygoodsecurity.vgscollect.view.card.validation.CardHolderValidator

/** @suppress */
internal class PersonNameInputField(context: Context): BaseInputField(context) {

    override var fieldType: FieldType = FieldType.CARD_HOLDER_NAME

    override fun applyFieldType() {
        val validator = CardHolderValidator()
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
    }
}
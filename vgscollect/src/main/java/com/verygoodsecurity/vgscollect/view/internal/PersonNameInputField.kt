package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import android.os.Build
import android.text.InputFilter
import android.text.InputType
import android.view.View
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.conection.InputCardHolderConnection
import com.verygoodsecurity.vgscollect.view.card.validation.RegexValidator

/** @suppress */
internal class PersonNameInputField(context: Context) : BaseInputField(context) {

    init {
        validator.addRule(RegexValidator(context.getString(R.string.validation_regex_person)))
    }

    override var fieldType: FieldType = FieldType.CARD_HOLDER_NAME

    override fun applyFieldType() {
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
        if (type == InputType.TYPE_TEXT_VARIATION_PASSWORD || type == InputType.TYPE_NUMBER_VARIATION_PASSWORD) {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        } else {
            inputType = InputType.TYPE_CLASS_TEXT
        }
        refreshInput()
    }

    override fun setupAutofill() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setAutofillHints(View.AUTOFILL_HINT_NAME)
        }
    }

}
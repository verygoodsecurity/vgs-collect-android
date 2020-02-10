package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import android.os.Build
import android.text.InputFilter
import android.text.InputType
import android.view.Gravity
import android.view.View
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.InputCardExpDateConnection
import com.verygoodsecurity.vgscollect.view.card.text.ExpirationDateTextWatcher
import com.verygoodsecurity.vgscollect.view.card.validation.CardExpDateValidator

internal class DateInputField(context: Context): BaseInputField(context) {

    override var fieldType: FieldType = FieldType.CARD_EXPIRATION_DATE

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if(isRTL()) {
            hasRTL = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                layoutDirection = View.LAYOUT_DIRECTION_LTR
                textDirection = View.TEXT_DIRECTION_LTR
            }
            gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
        }
    }

    override fun onAttachedToWindow() {
        isListeningPermitted = true
        applyFieldType()
        super.onAttachedToWindow()
        isListeningPermitted = false
    }

    override fun applyFieldType() {
        validator = CardExpDateValidator()
        inputConnection = InputCardExpDateConnection(id, validator)

        val str = text.toString()
        val stateContent = FieldContent.InfoContent().apply {
            this.data = str
        }
        val state = collectCurrentState(stateContent)

        inputConnection?.setOutput(state)
        inputConnection?.setOutputListener(stateListener)

        applyNewTextWatcher(ExpirationDateTextWatcher)
        val filterLength = InputFilter.LengthFilter(5)
        filters = arrayOf(filterLength)
        applyInputType()
    }

    private fun applyInputType() {
        val type = inputType
        if(type == InputType.TYPE_TEXT_VARIATION_PASSWORD || type == InputType.TYPE_NUMBER_VARIATION_PASSWORD) {
            inputType = InputType.TYPE_CLASS_DATETIME or InputType.TYPE_TEXT_VARIATION_PASSWORD
        } else {
            inputType = InputType.TYPE_CLASS_DATETIME
        }
        refreshInput()
    }
}
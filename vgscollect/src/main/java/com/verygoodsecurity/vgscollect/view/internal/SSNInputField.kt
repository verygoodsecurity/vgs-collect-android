package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.Gravity
import android.view.View
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.conection.InputSSNConnection
import com.verygoodsecurity.vgscollect.view.card.formatter.Formatter
import com.verygoodsecurity.vgscollect.view.card.formatter.SSNumberFormatter
import com.verygoodsecurity.vgscollect.view.card.validation.CompositeValidator
import com.verygoodsecurity.vgscollect.view.card.validation.LengthValidator
import com.verygoodsecurity.vgscollect.view.card.validation.MutableValidator
import com.verygoodsecurity.vgscollect.view.card.validation.RegexValidator

internal class SSNInputField(context: Context): BaseInputField(context) {

    override var fieldType: FieldType = FieldType.SSN

    private var divider:String = SPACE

    private val validator: MutableValidator = CompositeValidator()

    private var numberFormatter: Formatter? = null

    override fun applyFieldType() {
        validator.addRule(LengthValidator(MAX_LENGTH))
        validator.addRule(RegexValidator(VALIDATION_REGEX))
        inputConnection = InputSSNConnection(id, validator)

        val str = text.toString()
        val stateContent = FieldContent.SSNContent().apply {
            rawData = str.replace(divider, EMPTY_CHAR)
            this.data = str
        }
        val state = collectCurrentState(stateContent)

        inputConnection?.setOutput(state)
        inputConnection?.setOutputListener(stateListener)

        filters = arrayOf()

        applyFormatter()
        setupKeyListener()
        applyInputType()
    }

    private fun applyFormatter() {
        numberFormatter = with(SSNumberFormatter()) {
            setMask(MASK)
            applyNewTextWatcher(this)
            this
        }
    }

    private fun setupKeyListener() {
        val digits = resources.getString(R.string.card_number_digits) + this@SSNInputField.divider
        keyListener = DigitsKeyListener.getInstance(digits)
    }

    override fun updateTextChanged(str: String) {
        inputConnection?.also {
            with(it.getOutput()) {
                if (str.isNotEmpty()) {
                    hasUserInteraction = true
                }
                content = createCardNumberContent(str)
            }
        }?.also {
            handlerLooper.removeCallbacks(it)
            handlerLooper.postDelayed(it, REFRESH_DELAY)
        }
    }

    private fun createCardNumberContent(str: String): FieldContent.SSNContent {
        val c = FieldContent.SSNContent()
        c.rawData = str.replace(divider, EMPTY_CHAR)
        c.data = str
        return c
    }

    private fun applyInputType() {
        if(!isValidInputType(inputType)) {
            inputType = InputType.TYPE_CLASS_NUMBER
        }
        refreshInput()
    }

    private fun isValidInputType(type: Int):Boolean {
        return type == InputType.TYPE_CLASS_NUMBER ||
                type == InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
    }

    override fun setInputType(type: Int) {
        val validType = validateInputType(type)
        super.setInputType(validType)
        refreshInput()
    }

    private fun validateInputType(type: Int):Int {
        return when(type) {
            InputType.TYPE_CLASS_NUMBER -> type
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_NUMBER_VARIATION_PASSWORD -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            InputType.TYPE_NUMBER_VARIATION_PASSWORD -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD -> type
            else -> InputType.TYPE_CLASS_NUMBER
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if(isRTL()) {
            hasRTL = true
            layoutDirection = View.LAYOUT_DIRECTION_LTR
            textDirection = View.TEXT_DIRECTION_LTR
            gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
        }
    }

    companion object {
        private const val MASK = "###-##-####"
        private const val MAX_LENGTH = 11
        private const val VALIDATION_REGEX =  "^(?!\\b(\\d)\\1+\\b)(?!(123456789|219099999|078051120|457555462))(?!(000|666|9))(\\d{3}\\D?(?!(00))\\d{2}\\D?(?!(0000))\\d{4})\$"
        private const val SPACE =  "-"
        private const val EMPTY_CHAR =  ""
    }
}
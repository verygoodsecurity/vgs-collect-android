package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.Gravity
import android.view.View
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.util.extension.isNumeric
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.conection.InputSSNConnection
import com.verygoodsecurity.vgscollect.view.card.formatter.Formatter
import com.verygoodsecurity.vgscollect.view.card.formatter.SSNumberFormatter
import com.verygoodsecurity.vgscollect.view.card.validation.CompositeValidator
import com.verygoodsecurity.vgscollect.view.card.validation.LengthValidator
import com.verygoodsecurity.vgscollect.view.card.validation.MutableValidator
import com.verygoodsecurity.vgscollect.view.card.validation.RegexValidator
import com.verygoodsecurity.vgscollect.widget.SSNEditText.Companion.TAG

internal class SSNInputField(context: Context) : BaseInputField(context) {

    override var fieldType: FieldType = FieldType.SSN

    private var divider: String = DIVIDER
    private var ssnNumberMask = MASK

    private val validator: MutableValidator = CompositeValidator()

    private var numberFormatter: Formatter? = null

    override fun applyFieldType() {
        prepareValidation()
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

    private fun prepareValidation() {
        validator.clearRules()
        validator.addRule(LengthValidator(ssnNumberMask.length))
        validator.addRule(RegexValidator(VALIDATION_REGEX))
    }

    private fun applyFormatter() {
        numberFormatter = SSNumberFormatter().also {
            it.setMask(ssnNumberMask)
            applyNewTextWatcher(it)
        }
    }

    private fun applyDividerOnMask() {
        ssnNumberMask = MASK.run {
            replace(Regex(MASK_REGEX), divider)
        }

        if (!text.isNullOrEmpty() && numberFormatter?.getMask() != ssnNumberMask) {
            numberFormatter?.setMask(ssnNumberMask)
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
            it.run()
        }
    }

    private fun createCardNumberContent(str: String): FieldContent.SSNContent {
        val c = FieldContent.SSNContent()
        c.rawData = str.replace(divider, EMPTY_CHAR)
        c.data = str
        return c
    }

    private fun applyInputType() {
        if (!isValidInputType(inputType)) {
            inputType = InputType.TYPE_CLASS_NUMBER
        }
        refreshInput()
    }

    private fun isValidInputType(type: Int): Boolean {
        return type == InputType.TYPE_CLASS_NUMBER ||
                type == InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
    }

    override fun setInputType(type: Int) {
        val validType = validateInputType(type)
        super.setInputType(validType)
        refreshInput()
    }

    private fun validateInputType(type: Int): Int {
        return when (type) {
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
        if (isRTL()) {
            hasRTL = true
            layoutDirection = View.LAYOUT_DIRECTION_LTR
            textDirection = View.TEXT_DIRECTION_LTR
            gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
        }
    }

    internal fun setNumberDivider(divider: String?) {
        when {
            divider.isNullOrEmpty() -> this@SSNInputField.divider = EMPTY_CHAR
            divider.isNumeric() -> printWarning(TAG, R.string.error_divider_card_number_field)
            divider.length > 1 -> printWarning(TAG, R.string.error_divider_count_card_number_field)
            else -> this@SSNInputField.divider = divider
        }

        applyDividerOnMask()
        setupKeyListener()
        refreshInputConnection()
    }

    companion object {
        private const val MASK = "###-##-####"
        private const val MASK_REGEX = "[^#]"
        private const val MAX_LENGTH = 11
        internal const val VALIDATION_REGEX = "^(?!\\b(\\d)\\1+\\b)(?!(123-45-6789|219-09-9999|219-09-9999|457-55-5462))(?!(000|666|9))(\\d{3}\\D?(?!(00))\\d{2}\\D?(?!(0000))\\d{4})\$"
        private const val DIVIDER = "-"
        private const val EMPTY_CHAR = ""
    }
}
package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import android.text.InputType
import android.text.method.DigitsKeyListener
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.util.extension.formatDigits
import com.verygoodsecurity.vgscollect.util.extension.isNumeric
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.conection.InputSSNConnection
import com.verygoodsecurity.vgscollect.view.card.formatter.digit.DigitInputFormatter
import com.verygoodsecurity.vgscollect.view.card.validation.LengthMatchValidator
import com.verygoodsecurity.vgscollect.view.card.validation.RegexValidator
import com.verygoodsecurity.vgscollect.widget.SSNEditText.Companion.DIVIDER
import com.verygoodsecurity.vgscollect.widget.SSNEditText.Companion.TAG

internal class SSNInputField(context: Context) : BaseInputField(context) {

    override var fieldType: FieldType = FieldType.SSN

    private var divider: String = DIVIDER
    private var outputDivider: String = DIVIDER
    private var derivedNumberMask = MASK

    private var fromatter: DigitInputFormatter? = null

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
        inputConnection?.addOutputListener(stateListener)

        filters = arrayOf()

        applyFormatter()
        setupKeyListener()
        applyInputType()
    }

    private fun prepareValidation() {
        validator.clearRules()
        validator.addRule(LengthMatchValidator(derivedNumberMask.length))
        validator.addRule(RegexValidator(VALIDATION_REGEX))
    }

    private fun applyFormatter() {
        fromatter = DigitInputFormatter(derivedNumberMask).also { applyNewTextWatcher(it) }
    }

    private fun applyDividerOnMask() {
        val newNumberMask = MASK.run {
            replace(Regex(MASK_REGEX), divider)
        }

        if (fromatter?.getMask() != newNumberMask) {
            derivedNumberMask = newNumberMask
            fromatter?.setMask(newNumberMask)
            refreshOutputContent()
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
                content = createSSNContent(str)
            }
            it.run()
        }
    }

    private fun createSSNContent(str: String): FieldContent.SSNContent {
        val c = FieldContent.SSNContent()
        c.rawData = MASK.replace(DIVIDER, outputDivider).run { str.formatDigits(this).first }
        c.data = str
        c.vaultStorage = vaultStorage
        c.vaultAliasFormat = vaultAliasFormat
        c.isEnabledTokenization = isEnabledTokenization
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

    internal fun getOutputDivider(): Char? {
        return outputDivider.firstOrNull()
    }

    internal fun setOutputNumberDivider(divider: String?) {
        when {
            divider.isNullOrEmpty() -> outputDivider = EMPTY_CHAR
            arrayOf("#", "\\").contains(divider) -> printWarning(
                TAG,
                R.string.error_output_divider_mask
            ).also {
                outputDivider = DIVIDER
            }
            divider.isNumeric() -> printWarning(
                TAG,
                R.string.error_output_divider_number_field
            ).also {
                outputDivider = DIVIDER
            }
            divider.length > 1 -> printWarning(
                TAG,
                R.string.error_output_divider_count_number_field
            ).also {
                outputDivider = DIVIDER
            }
            else -> outputDivider = divider
        }
        refreshOutputContent()
    }

    internal fun getNumberDivider(): Char? {
        return divider.firstOrNull()
    }

    internal fun setNumberDivider(divider: String?) {
        when {
            divider.isNullOrEmpty() -> this@SSNInputField.divider = EMPTY_CHAR
            arrayOf("#", "\\").contains(divider) -> printWarning(
                TAG,
                R.string.error_divider_mask
            ).also {
                this@SSNInputField.divider = DIVIDER
            }
            divider.isNumeric() -> printWarning(TAG, R.string.error_divider_number_field).also {
                this@SSNInputField.divider = DIVIDER
            }
            divider.length > 1 -> printWarning(
                TAG,
                R.string.error_divider_count_number_field
            ).also {
                this@SSNInputField.divider = DIVIDER
            }
            else -> this@SSNInputField.divider = divider
        }

        applyDividerOnMask()
        setupKeyListener()
        refreshInputConnection()
    }

    companion object {
        private const val MASK = "###-##-####"
        private const val MASK_REGEX = "[^#]"
        internal const val VALIDATION_REGEX = "^(?!(000|666|9))(\\d{3}(-|\\s)?(?!(00))\\d{2}(-|\\s)?(?!(0000))\\d{4})\$"
        private const val EMPTY_CHAR = ""
    }
}
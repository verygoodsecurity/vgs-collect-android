package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import android.graphics.Rect
import android.text.InputType
import android.text.InputFilter
import android.text.method.DigitsKeyListener
import android.view.Gravity
import android.view.View
import com.verygoodsecurity.vgscollect.*
import com.verygoodsecurity.vgscollect.core.model.state.Dependency
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.storage.DependencyType
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.card.*
import com.verygoodsecurity.vgscollect.view.card.conection.InputCardCVCConnection
import com.verygoodsecurity.vgscollect.view.card.conection.InputCardExpDateConnection
import com.verygoodsecurity.vgscollect.view.card.conection.InputCardHolderConnection
import com.verygoodsecurity.vgscollect.view.card.conection.InputCardNumberConnection
import com.verygoodsecurity.vgscollect.view.card.conection.InputInfoConnection
import com.verygoodsecurity.vgscollect.view.card.filter.CardBrandFilter
import com.verygoodsecurity.vgscollect.view.card.filter.CardBrandPreview
import com.verygoodsecurity.vgscollect.view.card.filter.DefaultCardBrandFilter
import com.verygoodsecurity.vgscollect.view.card.filter.MutableCardFilter
import com.verygoodsecurity.vgscollect.view.card.formatter.CardNumberFormatter
import com.verygoodsecurity.vgscollect.view.card.icon.CardIconAdapter
import com.verygoodsecurity.vgscollect.view.card.text.CVCValidateFilter
import com.verygoodsecurity.vgscollect.view.card.text.ExpirationDateTextWatcher
import com.verygoodsecurity.vgscollect.view.card.validation.*
import com.verygoodsecurity.vgscollect.view.card.validation.CompositeValidator
import com.verygoodsecurity.vgscollect.view.card.validation.CardExpDateValidator

/** @suppress */
@Deprecated("This class is deprecated from 1.0.5")
internal class InputField(context: Context): BaseInputField(context),
    InputCardNumberConnection.IDrawCardBrand {

    companion object {
        fun getInputField(context: Context, parent: InputFieldView):BaseInputField {
            val field = InputField(context)
            field.vgsParent = parent
            return field
        }
    }

    private var cardtype: CardType = CardType.UNKNOWN

    private var iconAdapter = CardIconAdapter(context)

    private val userFilter: MutableCardFilter by lazy {
        CardBrandFilter(divider)
    }

    override var fieldType: FieldType = FieldType.INFO

    fun setType(type:FieldType) {
        fieldType = type
        applyFieldTypeConfigurations()
    }

    protected var validator: VGSValidator? = null

    private var divider:String? = " "

    private var iconGravity:Int = Gravity.NO_GRAVITY

    override fun onAttachedToWindow() {
        isListeningPermitted = true
        applyFieldTypeConfigurations()
        super.onAttachedToWindow()
        isListeningPermitted = false
    }

    override fun applyFieldType() {
        applyFieldTypeConfigurations()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if(isRTL()
            && (fieldType == FieldType.CARD_NUMBER
                    || fieldType == FieldType.CVC
                    || fieldType == FieldType.CARD_EXPIRATION_DATE)) {
            hasRTL = true
            layoutDirection = View.LAYOUT_DIRECTION_LTR
            textDirection = View.TEXT_DIRECTION_LTR
            gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
        }
    }

    private fun applyFieldTypeConfigurations() {
        when(fieldType) {
            FieldType.CARD_NUMBER -> applyCardNumFieldType()
            FieldType.CARD_EXPIRATION_DATE -> applyCardExpDateFieldType()
            FieldType.CARD_HOLDER_NAME -> applyCardHolderFieldType()
            FieldType.CVC -> applyCardCVCFieldType()
            FieldType.INFO -> applyInfoFieldType()
        }

        refreshInput()

        inputConnection?.run()
    }

    private fun applyInfoFieldType() {
        validator = InfoValidator()
        inputConnection =
            InputInfoConnection(
                id,
                validator
            )

        val str = text.toString()
        val stateContent = FieldContent.InfoContent().apply {
            this.data = str
        }
        val state = collectCurrentState(stateContent)

        inputConnection?.setOutput(state)
        inputConnection?.setOutputListener(stateListener)

        applyNewTextWatcher(null)
        applyTextInputType()
    }

    private fun applyCardExpDateFieldType() {
        validator = CardExpDateValidator()
        inputConnection =
            InputCardExpDateConnection(
                id,
                validator as CardExpDateValidator
            )

        val str = text.toString()
        val stateContent = FieldContent.InfoContent().apply {
            this.data = str
        }
        val state = collectCurrentState(stateContent)

        inputConnection?.setOutput(state)
        inputConnection?.setOutputListener(stateListener)

        applyNewTextWatcher(ExpirationDateTextWatcher())
        val filterLength = InputFilter.LengthFilter(5)
        filters = arrayOf(filterLength)
        applyDateInputType()
    }

    private fun applyCardHolderFieldType() {
        validator = RegexValidator("^[a-zA-Z0-9 ,'.-]+\$")
        inputConnection =
            InputCardHolderConnection(
                id,
                validator
            )

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
        applyTextInputType()
    }

    private fun applyCardCVCFieldType() {
        validator = CardCVCCodeValidator()
        inputConnection =
            InputCardCVCConnection(
                id,
                validator
            )

        val str = text.toString()
        val stateContent = FieldContent.InfoContent().apply {
            this.data = str
        }
        val state = collectCurrentState(stateContent)

        inputConnection?.setOutput(state)
        inputConnection?.setOutputListener(stateListener)

        applyNewTextWatcher(null)
        val filterLength = InputFilter.LengthFilter(4)
        filters = arrayOf(CVCValidateFilter(), filterLength)
        applyCVCInputType()
    }

    private fun applyCardNumFieldType() {
        val digits = resources.getString(R.string.card_number_digits) + this.divider
        keyListener = DigitsKeyListener.getInstance(digits)

        validator = CompositeValidator()

        inputConnection =
            InputCardNumberConnection(
                id,
                validator,
                this,
                divider
            )

        val defFilter = DefaultCardBrandFilter(CardType.values(), divider)
        inputConnection!!.addFilter(defFilter)
        inputConnection!!.addFilter(userFilter)

        val str = text.toString()
        val stateContent = FieldContent.CardNumberContent().apply {
            cardtype = this@InputField.cardtype
            this.data = str
        }
        val state = collectCurrentState(stateContent)

        inputConnection?.setOutput(state)
        inputConnection?.setOutputListener(stateListener)
        applyNewTextWatcher(CardNumberFormatter())
        applyNumberInputType()
    }

    override fun setTag(tag: Any?) {
        tag?.run {
            super.setTag(tag)
            inputConnection?.getOutput()?.fieldName = this as String
        }
    }

    override fun dispatchDependencySetting(dependency: Dependency) {
        when(dependency.dependencyType) {
            DependencyType.CARD -> manageLengthDependency(dependency)
            DependencyType.TEXT -> setText(dependency.value.toString())
        }
    }

    private fun manageLengthDependency(dependency: Dependency) {
        val cvcLength = (dependency.value as FieldContent.CardNumberContent).rangeCVV
        val filterLength = InputFilter.LengthFilter(cvcLength.last())
        filters = arrayOf(CVCValidateFilter(), filterLength)
        (inputConnection as? InputCardCVCConnection)?.defaultValidator = CardCVCCodeValidator(cvcLength)
        text = text
    }

    private fun applyCVCInputType() {
        if(!isValidNumberInputType(inputType)) {
            inputType = InputType.TYPE_CLASS_NUMBER
        }
        refreshInput()
    }

    private fun applyDateInputType() {
        if(!isValidDateInputType(inputType)) {
            inputType = InputType.TYPE_CLASS_DATETIME
        }
        refreshInput()
    }

    private fun applyTextInputType() {
        if(!isValidTextInputType(inputType)) {
            inputType = InputType.TYPE_CLASS_TEXT
        }
        refreshInput()
    }

    private fun applyNumberInputType() {
        if(!isValidNumberInputType(inputType)) {
            inputType = InputType.TYPE_CLASS_NUMBER
        }
        refreshInput()
    }

    override fun setInputType(type: Int) {
        val validType = when(fieldType) {
            FieldType.CARD_NUMBER -> validateNumberFieldType(type)
            FieldType.CARD_EXPIRATION_DATE -> validateDateFieldType(type)
            FieldType.CARD_HOLDER_NAME -> validateTextFieldType(type)
            FieldType.CVC -> validateNumberFieldType(type)
            FieldType.INFO -> validateTextFieldType(type)
            FieldType.SSN -> validateTextFieldType(type)
        }
        super.setInputType(validType)
        refreshInput()
    }
    private fun isValidNumberInputType(type: Int):Boolean {
        return type == InputType.TYPE_CLASS_NUMBER ||
                type == InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
    }
    private fun isValidDateInputType(type: Int):Boolean {
        return type == InputType.TYPE_CLASS_TEXT ||
                type == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                type == InputType.TYPE_CLASS_DATETIME
    }
    private fun isValidTextInputType(type: Int):Boolean {
        return type == InputType.TYPE_CLASS_TEXT ||
                type == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                type == InputType.TYPE_CLASS_DATETIME ||
                type == InputType.TYPE_CLASS_NUMBER ||
                type == InputType.TYPE_NUMBER_VARIATION_PASSWORD ||
                type == InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
    }

    private fun validateNumberFieldType(type: Int):Int {
        return when(type) {
            InputType.TYPE_CLASS_NUMBER -> type
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_NUMBER_VARIATION_PASSWORD -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            InputType.TYPE_NUMBER_VARIATION_PASSWORD -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD -> type
            else -> InputType.TYPE_CLASS_NUMBER
        }
    }
    private fun validateDateFieldType(type: Int):Int {
        return when(type) {
            InputType.TYPE_CLASS_TEXT -> type
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD -> type
            InputType.TYPE_CLASS_DATETIME -> type
            InputType.TYPE_CLASS_NUMBER -> InputType.TYPE_CLASS_DATETIME
            InputType.TYPE_NUMBER_VARIATION_PASSWORD -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD -> type
            else -> InputType.TYPE_CLASS_TEXT
        }
    }
    private fun validateTextFieldType(type: Int):Int {
        return when(type) {
            InputType.TYPE_CLASS_TEXT -> type
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD -> type
            InputType.TYPE_CLASS_DATETIME -> type
            InputType.TYPE_CLASS_NUMBER -> type
            InputType.TYPE_NUMBER_VARIATION_PASSWORD -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD -> type
            else -> InputType.TYPE_CLASS_TEXT
        }
    }

    override fun onCardBrandPreview(card: CardBrandPreview) {
        val r = Rect()
        getLocalVisibleRect(r)

        val cardPreview = iconAdapter.getItem(card.cardType, card.name, card.resId, r)

        when (iconGravity) {
            Gravity.LEFT -> setCompoundDrawables(cardPreview,null,null,null)
            Gravity.START -> setCompoundDrawables(cardPreview,null,null,null)
            Gravity.RIGHT -> setCompoundDrawables(null,null,cardPreview,null)
            Gravity.END -> setCompoundDrawables(null,null,cardPreview,null)
        }
    }
}
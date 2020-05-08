package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.text.InputType
import android.text.TextWatcher
import android.os.Looper
import android.text.InputFilter
import android.text.method.DigitsKeyListener
import android.view.Gravity
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.addTextChangedListener
import com.verygoodsecurity.vgscollect.*
import com.verygoodsecurity.vgscollect.core.model.state.Dependency
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.storage.DependencyType
import com.verygoodsecurity.vgscollect.view.card.*
import com.verygoodsecurity.vgscollect.view.card.filter.CardBrandFilter
import com.verygoodsecurity.vgscollect.view.card.filter.DefaultCardBrandFilter
import com.verygoodsecurity.vgscollect.view.card.filter.MutableCardFilter
import com.verygoodsecurity.vgscollect.view.card.text.CVCValidateFilter
import com.verygoodsecurity.vgscollect.view.card.text.CardNumberTextWatcher
import com.verygoodsecurity.vgscollect.view.card.text.ExpirationDateTextWatcher
import com.verygoodsecurity.vgscollect.view.card.validation.*
import com.verygoodsecurity.vgscollect.view.card.validation.card.CardNumberValidator
import com.verygoodsecurity.vgscollect.view.card.validation.CardExpDateValidator

/** @suppress */
@Deprecated("This class is deprecated from 1.0.5")
internal class InputField(context: Context): BaseInputField(context) {

    private var cardtype: CardType = CardType.NONE

    private val userFilter: MutableCardFilter by lazy {
        CardBrandFilter( this, divider)
    }

    override var fieldType: FieldType = FieldType.INFO

    fun setType(type:FieldType) {
        fieldType = type
        applyFieldTypeConfigurations()
    }

    protected var validator: VGSValidator? = null

    private var divider:String? = " "

    private var iconGravity:Int = Gravity.NO_GRAVITY

    init {
        isListeningPermitted = true
        onFocusChangeListener = OnFocusChangeListener { _, f ->
            inputConnection?.getOutput()?.isFocusable = f
            inputConnection?.run()
        }
        val handler = Handler(Looper.getMainLooper())
        addTextChangedListener {
            inputConnection?.getOutput()?.content?.data =  it.toString()

            handler.removeCallbacks(inputConnection)
            handler.postDelayed(inputConnection, 200)
        }
        isListeningPermitted = false
        id = ViewCompat.generateViewId()

        compoundDrawablePadding = resources.getDimension(R.dimen.half_vgsfield_padding).toInt()
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        setSelection(text?.length?:0)
    }

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                layoutDirection = View.LAYOUT_DIRECTION_LTR
                textDirection = View.TEXT_DIRECTION_LTR
            }
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
        applyTextInputType()
    }

    private fun applyCardExpDateFieldType() {
        validator = CardExpDateValidator()
        inputConnection = InputCardExpDateConnection(id, validator as CardExpDateValidator)

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
        applyTextInputType()
    }

    private fun applyCardCVCFieldType() {
        validator = CardCVCCodeValidator()
        inputConnection = InputCardCVCConnection(id, validator)

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

        validator = CardNumberValidator(divider)

        inputConnection = InputCardNumberConnection(id,
                validator,
                object :
                    InputCardNumberConnection.IdrawCardBrand {
                    override fun drawCardBrandPreview() {
                        this@InputField.drawCardBrandPreview()
                    }
                },
                divider)

        val defFilter = DefaultCardBrandFilter(CardType.values(), this, divider)
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
        applyNewTextWatcher(CardNumberTextWatcher(divider))    //fixme needTo apply TextWatcher
        applyNumberInputType()
    }

    private fun drawCardBrandPreview() {
        val state = inputConnection?.getOutput()

        var l: Drawable? = null
        var r: Drawable? = null

        val privaryRes = (state?.content as? FieldContent.CardNumberContent)?.iconResId

        when (iconGravity) {
            Gravity.LEFT -> l = getDrawable(privaryRes)
            Gravity.START -> l = getDrawable(privaryRes)
            Gravity.RIGHT -> r = getDrawable(privaryRes)
            Gravity.END -> r = getDrawable(privaryRes)
        }

        setCompoundDrawables(l,null,r,null)
    }

    private fun getDrawable(primaryRes:Int?): Drawable? {
        return primaryRes?.run {
            val cIconSizeW = resources.getDimension(R.dimen.c_icon_size_w).toInt()
            val cIconSizeH = resources.getDimension(R.dimen.c_icon_size_h).toInt()
            val drawable = ContextCompat.getDrawable(context, primaryRes)
            drawable?.setBounds(0, 0, cIconSizeW, cIconSizeH)
            return drawable
        }
    }

    override fun setCompoundDrawables(
        left: Drawable?,
        top: Drawable?,
        right: Drawable?,
        bottom: Drawable?
    ) {
        if(hasRTL) {
            super.setCompoundDrawables(right, top, left, bottom)
        } else {
            super.setCompoundDrawables(left, top, right, bottom)
        }
    }

    override fun setTag(tag: Any?) {
        tag?.run {
            super.setTag(tag)
            inputConnection?.getOutput()?.fieldName = this as String
        }
    }

    override fun addTextChangedListener(watcher: TextWatcher?) {
        if(isListeningPermitted) {
            super.addTextChangedListener(watcher)
        }
    }

    override fun dispatchDependencySetting(dependency: Dependency) {
        when(dependency.dependencyType) {
            DependencyType.LENGTH -> manageLengthDependency(dependency)
            DependencyType.TEXT -> setText(dependency.value.toString())
        }
    }

    private fun manageLengthDependency(dependency: Dependency) {
        val filterLength = InputFilter.LengthFilter(dependency.value as Int)
        filters = arrayOf(CVCValidateFilter(), filterLength)
        (inputConnection as? InputCardCVCConnection)?.runtimeValidator = CardCVCCodeValidator(dependency.value)
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
}
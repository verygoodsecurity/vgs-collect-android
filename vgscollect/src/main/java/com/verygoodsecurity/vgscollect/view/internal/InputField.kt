package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.text.InputType
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText
import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import android.os.Looper
import android.text.InputFilter
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.addTextChangedListener
import com.verygoodsecurity.vgscollect.*
import com.verygoodsecurity.vgscollect.core.model.state.Dependency
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.core.storage.DependencyListener
import com.verygoodsecurity.vgscollect.core.storage.DependencyType
import com.verygoodsecurity.vgscollect.util.Logger
import com.verygoodsecurity.vgscollect.view.card.*
import com.verygoodsecurity.vgscollect.view.card.filter.CardBrandFilter
import com.verygoodsecurity.vgscollect.view.card.filter.DefaultCardBrandFilter
import com.verygoodsecurity.vgscollect.view.card.filter.MutableCardFilter
import com.verygoodsecurity.vgscollect.view.card.text.CVCValidateFilter
import com.verygoodsecurity.vgscollect.view.card.text.CardNumberTextWatcher
import com.verygoodsecurity.vgscollect.view.card.text.ExpirationDateTextWatcher
import com.verygoodsecurity.vgscollect.view.card.validation.*
import com.verygoodsecurity.vgscollect.view.card.validation.card.CardNumberValidator

internal class InputField(context: Context): TextInputEditText(context),
    DependencyListener {

    var fieldType: FieldType = FieldType.INFO
        internal set

    private var cardtype: CardType = CardType.NONE

    private val userFilter: MutableCardFilter by lazy {
        CardBrandFilter( this, divider)
    }

    private var validator: VGSValidator? = null
    private var inputConnection: InputRunnable? = null

    private var isListeningPermitted = false
    private var isBackgroundVisible = true
    private var divider:String? = " "

    private var iconGravity:Int = Gravity.NO_GRAVITY

    private var activeTextWatcher: TextWatcher? = null
    internal var stateListener: OnVgsViewStateChangeListener? = null
        internal set(value) {
            field = value
            inputConnection?.setOutputListener(value)
        }

    internal var isRequired:Boolean = true
        set(value) {
            field = value
            inputConnection?.getOutput()?.isRequired = value
            inputConnection?.run()
        }

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
            handler.postDelayed(inputConnection, 300)
        }
        isListeningPermitted = false
        id = ViewCompat.generateViewId()

        compoundDrawablePadding = resources.getDimension(R.dimen.half_default_padding).toInt()
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        setSelection(text?.length?:0)
    }

    override fun onAttachedToWindow() {
        isListeningPermitted = true
        applyAttributes()
        super.onAttachedToWindow()
        isListeningPermitted = false
    }

    private var hasRTL = false
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

    private fun isRTL():Boolean {
        val direction = getResolvedLayoutDirection()
        return direction == View.LAYOUT_DIRECTION_RTL
                || direction == View.TEXT_DIRECTION_ANY_RTL
                || direction == View.TEXT_DIRECTION_FIRST_STRONG_RTL
                || direction == View.TEXT_DIRECTION_RTL
    }

    private fun getResolvedLayoutDirection():Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutDirection
        } else {
            View.LAYOUT_DIRECTION_LTR
        }
    }

    private fun applyAttributes() {
        when(fieldType) {
            FieldType.CARD_NUMBER -> applyCardNumFieldType()
            FieldType.CARD_EXPIRATION_DATE -> applyCardExpDateFieldType()
            FieldType.CARD_HOLDER_NAME -> applyCardHolderFieldType()
            FieldType.CVC -> applyCardCVCFieldType()
            FieldType.INFO -> applyInfoFieldType()
        }

        text = text

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
        applyNumberInputType()
    }

    private fun applyCardNumFieldType() {
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

    private fun applyNewTextWatcher(textWatcher: TextWatcher?) {
        activeTextWatcher?.let { removeTextChangedListener(activeTextWatcher) }
        textWatcher?.let { addTextChangedListener(textWatcher) }
        activeTextWatcher = textWatcher
    }

    private fun collectCurrentState(stateContent:FieldContent): VGSFieldState {
        val state = VGSFieldState().apply {
            isRequired = this@InputField.isRequired
            isFocusable = this@InputField.hasFocus()
            type = this@InputField.fieldType
            content = stateContent

            fieldName = this@InputField.tag as? String
        }

        return state
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

    private fun applyDateInputType() {
        val type = inputType
        if(type == InputType.TYPE_TEXT_VARIATION_PASSWORD || type == InputType.TYPE_NUMBER_VARIATION_PASSWORD) {
            inputType = InputType.TYPE_CLASS_DATETIME or InputType.TYPE_TEXT_VARIATION_PASSWORD
        } else {
            inputType = InputType.TYPE_CLASS_DATETIME
        }
    }

    private fun applyNumberInputType() {
        val type = inputType
        if(type == InputType.TYPE_TEXT_VARIATION_PASSWORD || type == InputType.TYPE_NUMBER_VARIATION_PASSWORD) {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        } else {
            inputType = InputType.TYPE_CLASS_TEXT
        }
    }

    private fun applyTextInputType() {
        val type = inputType
        if(type == InputType.TYPE_TEXT_VARIATION_PASSWORD || type == InputType.TYPE_NUMBER_VARIATION_PASSWORD) {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        } else {
            inputType = InputType.TYPE_CLASS_TEXT
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

    private fun setCursorDrawableColor(color: Int) {
        try {
            val cursorDrawableResField = TextView::class.java.getDeclaredField("mCursorDrawableRes")
            cursorDrawableResField.isAccessible = true
            val cursorDrawableRes = cursorDrawableResField.getInt(this)
            val editorField = TextView::class.java.getDeclaredField("mEditor")
            editorField.isAccessible = true
            val editor = editorField.get(this)
            val clazz = editor.javaClass
            val res = context.resources
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val drawableForCursorField = clazz.getDeclaredField("mDrawableForCursor")
                drawableForCursorField.isAccessible = true
                val drawable = res.getDrawable(cursorDrawableRes)
                drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                drawableForCursorField.set(editor, drawable)
            } else {
                val cursorDrawableField = clazz.getDeclaredField("mCursorDrawable")
                cursorDrawableField.isAccessible = true
                val drawables = arrayOfNulls<Drawable>(2)
                drawables[0] = res.getDrawable(cursorDrawableRes)
                drawables[0]?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                drawables[1] = res.getDrawable(cursorDrawableRes)
                drawables[1]?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                cursorDrawableField.set(editor, drawables)
            }
        } catch (t: Throwable) {
            Logger.i("VGSEditText", "Can't apply color on cursor")
        }
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        val minPaddingV = resources.getDimension(R.dimen.default_vertical_field).toInt()
        val minPaddingH = resources.getDimension(R.dimen.default_horizontal_field).toInt()
        val l = if(left < minPaddingH) minPaddingH else left
        val t = if(top < minPaddingV) minPaddingV else top
        val r = if(right < minPaddingH) minPaddingH else right
        val b = if(bottom < minPaddingV) minPaddingV else bottom
        super.setPadding(l, t, r, b)
    }

    internal fun setHasBackground(state:Boolean) {
        isBackgroundVisible = state
        if(isBackgroundVisible) {
            setBackgroundResource(android.R.color.transparent)
        }
    }

    internal fun setCardPreviewIconGravity(gravity:Int) {
        iconGravity = gravity
    }

    internal fun setCardBrand(c:CustomCardBrand) {
        userFilter.add(c)
        inputConnection?.run()
    }

    internal fun setNumberDivider(divider: String?) {
        when {
            divider.isNullOrEmpty() -> this@InputField.divider = ""
            divider.length == 1 -> this@InputField.divider = divider
            else -> Logger.i("VGSEditTextView", "Divider for number can't be greater than 1 symbol. (${divider})")
        }
    }

    override fun dispatchDependencySetting(dependency: Dependency) {
        when(dependency.dependencyType) {
            DependencyType.LENGTH -> {
                val filterLength = InputFilter.LengthFilter(dependency.value as Int)
                filters = arrayOf(CVCValidateFilter(), filterLength)
                text = text
            }
            DependencyType.TEXT -> setText(dependency.value.toString())
        }
    }
}
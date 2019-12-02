package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText
import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.view.text.validation.card.*
import android.os.Looper
import android.view.Gravity
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.addTextChangedListener
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.util.Logger
import com.verygoodsecurity.vgscollect.view.text.validation.card.VGSEditTextFieldType

internal class EditTextWrapper(context: Context): TextInputEditText(context) {

    private var vgsFieldType: VGSEditTextFieldType? = null
    private val state = VGSFieldState()

    private var isListeningPermitted = false
    private var isBackgroundVisible = true

    private var iconGravity:Int = Gravity.NO_GRAVITY

    private var activeTextWatcher: TextWatcher? = null
    internal var stateListener: OnVgsViewStateChangeListener? = null
        internal set(value) {
            field = value
            field?.emit(id, state)
        }

    internal var isRequired:Boolean = true
        set(value) {
            field = value
            state.isRequired = value
            stateListener?.emit(id, state)
        }

    private val inputStateRunnable = Runnable {
        updateCompoundCardPreview()

        vgsFieldType?.validate(state.content)        //fixme change place to detect card type

        vgsFieldType?.let { state.type = it }

        stateListener?.emit(id, state)
    }

    init {
        isListeningPermitted = true
        onFocusChangeListener = OnFocusChangeListener { _, f ->
            state.isFocusable = f
            stateListener?.emit(id, state)
        }

        val handler = Handler(Looper.getMainLooper())
        addTextChangedListener {
            state.content = it.toString()
            handler.removeCallbacks(inputStateRunnable)
            handler.postDelayed(inputStateRunnable, 300)
        }
        isListeningPermitted = false
        id = ViewCompat.generateViewId()

        compoundDrawablePadding = resources.getDimension(R.dimen.half_default_padding).toInt()
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        setSelection(text?.length?:0)
    }

    fun setFieldType(fieldType: FieldType) {
        isListeningPermitted = true
        when(fieldType) {
            FieldType.CARD_NUMBER -> applyCardNumFieldType()
            FieldType.CARD_EXPIRATION_DATE -> applyCardExpDateFieldType()
            FieldType.CARD_HOLDER_NAME -> applyCardHolderFieldType()
            FieldType.CVC -> applyCardCVCFieldType()
            FieldType.INFO -> applyInfoFieldType()
        }

        state.type = vgsFieldType!!
        stateListener?.emit(id, state)
        isListeningPermitted = false
        setText(text)
    }

    private fun applyInfoFieldType() {
        vgsFieldType = VGSEditTextFieldType.Info
        applyNewTextWatcher(null)
        filters = arrayOf()
        applyTextInputType()
    }

    private fun applyCardExpDateFieldType() {
        vgsFieldType = VGSEditTextFieldType.CardExpDate
        applyNewTextWatcher(ExpirationDateTextWatcher)
        val filterLength = InputFilter.LengthFilter(vgsFieldType!!.length)
        filters = arrayOf(filterLength)
        applyTextInputType()
    }

    private fun applyCardHolderFieldType() {
        vgsFieldType = VGSEditTextFieldType.CardHolderName
        applyNewTextWatcher(null)
        val filterLength = InputFilter.LengthFilter(vgsFieldType!!.length)
        filters = arrayOf(filterLength)
        applyTextInputType()
    }

    private fun applyCardCVCFieldType() {
        vgsFieldType = VGSEditTextFieldType.CVCCardCode
        applyNewTextWatcher(null)
        val filterLength = InputFilter.LengthFilter(vgsFieldType!!.length)
        filters = arrayOf(CVCValidateFilter(), filterLength)
        applyNumberInputType()
    }

    private fun applyCardNumFieldType() {
        vgsFieldType = VGSEditTextFieldType.CardNumber()
        applyNewTextWatcher(CardNumberTextWatcher)
        val filter = InputFilter.LengthFilter(vgsFieldType!!.length)
        filters = arrayOf(filter)
        applyTextInputType()
    }

    private fun applyNumberInputType() {
        val type = inputType
        if(type == InputType.TYPE_TEXT_VARIATION_PASSWORD || type == InputType.TYPE_NUMBER_VARIATION_PASSWORD) {
            setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
        } else {
            setInputType(InputType.TYPE_CLASS_TEXT)
        }
    }

    private fun applyTextInputType() {
        val type = inputType
        if(type == InputType.TYPE_TEXT_VARIATION_PASSWORD || type == InputType.TYPE_NUMBER_VARIATION_PASSWORD) {
            setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
        } else {
            setInputType(InputType.TYPE_CLASS_TEXT)
        }
    }

    override fun setTag(tag: Any?) {
        tag?.run {
            super.setTag(tag)
            state.fieldName = this as String
        }
    }

    override fun addTextChangedListener(watcher: TextWatcher?) {
        if(isListeningPermitted) {
            super.addTextChangedListener(watcher)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isListeningPermitted = false
    }

    private fun applyNewTextWatcher(textWatcher: TextWatcher?) {
        activeTextWatcher?.let { removeTextChangedListener(activeTextWatcher) }
        textWatcher?.let { addTextChangedListener(textWatcher) }
        activeTextWatcher = textWatcher
    }

    internal fun setCursorDrawableColor(color: Int) {
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
            Logger.i("VGSEditText", "can't apply color on cursor")
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

    fun setHasBackground(state:Boolean) {
        isBackgroundVisible = state
        if(isBackgroundVisible) {
            setBackgroundResource(android.R.color.transparent)
        }
    }

    private fun updateCompoundCardPreview() {
        var l: Drawable? = null
        var r: Drawable? = null

        if (vgsFieldType is VGSEditTextFieldType.CardNumber) {
            val str = text.toString().replace(" ", "")
            var privaryRes = 0
            val v = CardType.values()
            for(i in v.indices) {
                val type = v[i]

               if (type.isValid(str)) {
                    privaryRes = type.resId
                    break
                }
            }

            when (iconGravity) {
                Gravity.LEFT -> l = ContextCompat.getDrawable(context, privaryRes)
                Gravity.START -> l = ContextCompat.getDrawable(context, privaryRes)
                Gravity.RIGHT -> r = ContextCompat.getDrawable(context, privaryRes)
                Gravity.END -> r = ContextCompat.getDrawable(context, privaryRes)
            }

            val cIconWidth = resources.getDimension(R.dimen.c_icon_width).toInt()
            val cIconHeight = resources.getDimension(R.dimen.c_icon_height).toInt()
            r?.setBounds(0, 0, cIconWidth, cIconHeight)
            l?.setBounds(0, 0, cIconWidth, cIconHeight)
        }
        setCompoundDrawables(l,null,r,null)
    }

    fun setCardPreviewIconGravity(gravity:Int) {
        iconGravity = gravity
    }
}
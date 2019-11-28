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
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.widget.addTextChangedListener
import com.verygoodsecurity.vgscollect.util.Logger
import com.verygoodsecurity.vgscollect.view.text.validation.card.VGSTextInputType

internal class EditTextWrapper(context: Context): TextInputEditText(context) {

    private var vgsInputType: VGSTextInputType? = null
    private val state = VGSFieldState()

    private var isListeningPermitted = false

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
        vgsInputType?.validate(state.content)        //fixme change place to detect card type

        vgsInputType?.let { state.type = it }

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
            handler.postDelayed(inputStateRunnable, 500)
        }
        isListeningPermitted = false
        id = ViewCompat.generateViewId()
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        setSelection(text?.length?:0)
    }

    fun setFieldType(fieldType: VGSTextInputType) {
        isListeningPermitted = true
        vgsInputType = fieldType
        when(fieldType) {
            is VGSTextInputType.CardNumber -> applyCardNumFieldType(fieldType.length)
            is VGSTextInputType.CVCCardCode -> applyCardCVCFieldType(fieldType.length)
            is VGSTextInputType.CardHolderName -> applyCardHolderFieldType(fieldType.length)
            is VGSTextInputType.CardExpDate -> applyCardExpDateFieldType(fieldType.length)
        }
        state.type = vgsInputType!!
        stateListener?.emit(id, state)
        isListeningPermitted = false
        setText(text)
    }

    private fun applyCardExpDateFieldType(length: Int) {
        applyNewTextWatcher(ExpirationDateTextWatcher)
        val filterLength = InputFilter.LengthFilter(length)
        filters = arrayOf(filterLength)
        applyTextInputType()
    }

    private fun applyCardHolderFieldType(length: Int) {
        applyNewTextWatcher(null)
        filters = arrayOf()
        applyTextInputType()
    }

    private fun applyCardCVCFieldType(length: Int) {
        applyNewTextWatcher(null)
        val filterLength = InputFilter.LengthFilter(length)
        filters = arrayOf(CVCValidateFilter(), filterLength)
        applyNumberInputType()
    }

    private fun applyCardNumFieldType(length: Int) {
        applyNewTextWatcher(CardNumberTextWatcher)
        val filter = InputFilter.LengthFilter(length)
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
}
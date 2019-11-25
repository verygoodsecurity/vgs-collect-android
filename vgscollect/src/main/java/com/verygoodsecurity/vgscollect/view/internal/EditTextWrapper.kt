package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import android.os.Handler
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText
import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.view.text.validation.card.*
import android.os.Looper
import androidx.core.view.ViewCompat
import androidx.core.widget.addTextChangedListener
import com.verygoodsecurity.vgscollect.view.text.validation.card.VGSTextInputType

internal class EditTextWrapper(context: Context): TextInputEditText(context) {

    private var vgsInputType: VGSTextInputType = VGSTextInputType.CardOwnerName
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
        vgsInputType.validate(state.content)        //fixme change place to detect card type

        state.type = vgsInputType
        stateListener?.emit(id, state)
    }

    init {
        isFocusable = false
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

    fun setFieldType(inputType: VGSTextInputType) {
        isListeningPermitted = true
        vgsInputType = inputType
        when(inputType) {
            is VGSTextInputType.CardNumber -> {
                applyNewTextWatcher(CardNumberTextWatcher)
                val filter = InputFilter.LengthFilter(inputType.length)
                filters = arrayOf(filter)
                setInputType(InputType.TYPE_CLASS_PHONE)
            }
            is VGSTextInputType.CVCCardCode -> {
                applyNewTextWatcher(null)
                val filterLength = InputFilter.LengthFilter(inputType.length)
                filters = arrayOf(CVCValidateFilter(), filterLength)
                setInputType(InputType.TYPE_CLASS_DATETIME)
            }
            is VGSTextInputType.CardOwnerName -> {
                applyNewTextWatcher(null)
                filters = arrayOf()
                setInputType(InputType.TYPE_CLASS_TEXT)
            }
            is VGSTextInputType.CardExpDate -> {
                applyNewTextWatcher(ExpirationDateTextWatcher)
                val filterLength = InputFilter.LengthFilter(inputType.length)
                filters = arrayOf(filterLength)
                setInputType(InputType.TYPE_CLASS_DATETIME)
            }
        }
        state.type = vgsInputType
        stateListener?.emit(id, state)
        isListeningPermitted = false
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
}
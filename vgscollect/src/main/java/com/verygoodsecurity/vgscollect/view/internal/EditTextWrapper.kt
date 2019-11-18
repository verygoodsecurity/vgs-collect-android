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
import android.text.Editable
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.inputmethod.ExtractedText
import androidx.core.view.ViewCompat
import androidx.core.widget.addTextChangedListener
import com.verygoodsecurity.vgscollect.view.text.validation.card.VGSTextInputType

internal class EditTextWrapper(context: Context): TextInputEditText(context) {

    private var vgsInputType: VGSTextInputType = VGSTextInputType.CardOwnerName
    private val state = VGSFieldState()

//    private var isListeningPermitted = false

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
//        isListeningPermitted = true
        onFocusChangeListener = OnFocusChangeListener { _, f ->
            state.isFocusable = f
            stateListener?.emit(id, state)
        }

        val handler = Handler(Looper.getMainLooper())
        addTextChangedListener {
//            if(vgsInputType is VGSTextInputType.CardNumber) {
//                state.content = it.toString().replace(" ".toRegex(), "")
//            } else {
//                state.content = it.toString()
//            }
            state.content = it.toString()
            handler.removeCallbacks(inputStateRunnable)
            handler.postDelayed(inputStateRunnable, 500)
        }
//        isListeningPermitted = false
        id = ViewCompat.generateViewId()
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
//        if(vgsInputType is VGSTextInputType.CardExpDate)  //todo add possibility to set default cursor position
        setSelection(text?.length?:0)
    }

    fun setFieldType(inputType: VGSTextInputType) {
        vgsInputType = inputType
        when(inputType) {
            is VGSTextInputType.CardNumber -> {
                applyNewTextWatcher(CardNumberTextWatcher)
                val filter = InputFilter.LengthFilter(inputType.length)
                filters = arrayOf(filter)
                setInputType(InputType.TYPE_CLASS_PHONE)
            }
            is VGSTextInputType.CVVCardCode -> {
                applyNewTextWatcher(null)
                val filterLength = InputFilter.LengthFilter(inputType.length)
                filters = arrayOf(CVVValidateFilter(), filterLength)
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
    }

    override fun setTag(tag: Any?) {
        tag?.run {
            super.setTag(tag)
            state.alias = this as String
        }
    }

    override fun setSelection(index: Int) {
        isPermited = true
        Log.e("test", "setSelection-1----")
        super.setSelection(index)
    }

    override fun setSelection(start: Int, stop: Int) {
//        counter+=1
        isPermited = true
        Log.e("test", "setSelection-2---")
        super.setSelection(start, stop)
    }

    override fun selectAll() {
        Log.e("test", "selectAll")
        super.selectAll()
    }

    override fun extendSelection(index: Int) {
        Log.e("test", "extendSelection $index")
        super.extendSelection(index)
    }

    override fun setExtractedText(text: ExtractedText?) {
        Log.e("test", "setExtractedText $text")
        super.setExtractedText(text)
    }

    override fun getSelectionStart(): Int {
        isPermited = true
        counter+=1
        Log.e("test", "getSelectionStart $counter")
        return super.getSelectionStart()
    }



    override fun getSelectionEnd(): Int {
        val s = super.getSelectionEnd()
        counter-=1
        if(counter <= 0) {
            counter = 0
//            isPermited = false
        }
        Log.e("test", "getSelectionEnd $isPermited  $counter $s")
        return s
    }

    override fun onPopulateAccessibilityEvent(event: AccessibilityEvent?) {
        Log.e("test", "onPopulateAccessibilityEvent")
        super.onPopulateAccessibilityEvent(event)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.e("test", "onMeasure desired")
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        counter = 0
    }


    override fun setText(text: CharSequence?, type: BufferType?) {
        Log.e("test", "S setText $isPermited $text")
        super.setText(text, type)
    }

private var isPermited = true
    private var counter = 0
    override fun getText(): Editable? {
        counter-=1
        if(counter <= 0) {
            counter = 0
            isPermited = false
        }
        if(isPermited) {
            Log.e("test", "1getText $isPermited")

            return super.getText()
        } else {
            val tLength = super.getText()?.length ?: 0
            val mask = "#".repeat(tLength)
            Log.e("test", "2getText $isPermited")
            return Editable.Factory.getInstance().newEditable(mask)
        }
    }


    override fun getEditableText(): Editable {
        if(isPermited) {
            return super.getEditableText()
        } else {
            val tLength = super.getEditableText()?.length?:0

            val mask = "#".repeat(tLength)
            return Editable.Factory.getInstance().newEditable( mask)
        }
    }

    override fun addTextChangedListener(watcher: TextWatcher?) {
        if(isPermited) {
            super.addTextChangedListener(watcher)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isPermited = false
    }

    private fun applyNewTextWatcher(textWatcher: TextWatcher?) {
        activeTextWatcher?.let { removeTextChangedListener(activeTextWatcher) }
        textWatcher?.let { addTextChangedListener(textWatcher) }
        activeTextWatcher = textWatcher
    }

//    internal fun setVGSPlaceHolderText(text:String?) {
//        hint = text
//        state.placeholder = text
//        stateListener?.emit(id, state)
//    }
}
package com.verygoodsecurity.vgscollect.view

import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import androidx.core.view.ViewCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.model.VGSFieldState
import com.verygoodsecurity.vgscollect.view.text.validation.card.CardNumberTextWatcher
import com.verygoodsecurity.vgscollect.view.text.validation.card.ExpirationDateeTextwatcher

internal class EditTextWrapper(context: Context): TextInputEditText(context) {

    private var vgsInputType: VGSTextInputType = VGSTextInputType.InfoField
    private val state = VGSFieldState()

    private var activeTextWatcher: TextWatcher? = null
    private var stateListener: OnVgsViewStateChangeListener? = null

    init {
        onFocusChangeListener = OnFocusChangeListener { v, f ->
            state.isFocusable = f
            stateListener?.emit(id, state)
        }
        addTextChangedListener {
            state.content = it.toString()
            stateListener?.emit(id, state)
        }
        id = ViewCompat.generateViewId()
    }

    fun setInputFormatType(inputType: VGSTextInputType) {
        vgsInputType = inputType
        when(inputType) {
            is VGSTextInputType.CardNumber -> {
                applyNewTextWatcher(CardNumberTextWatcher)
                val filter = InputFilter.LengthFilter(inputType.length)
                filters = arrayOf(filter)
                setInputType(InputType.TYPE_CLASS_PHONE)
            }
            is VGSTextInputType.CVVCardCode -> {
                applyNewTextWatcher(CardNumberTextWatcher)
                val filter = InputFilter.LengthFilter(inputType.length)
                filters = arrayOf(filter)
                setInputType(InputType.TYPE_CLASS_NUMBER)
            }
            is VGSTextInputType.CardOwnerName -> {
                applyNewTextWatcher(null)
                val filter = InputFilter.LengthFilter(inputType.length)
                filters = arrayOf(filter)
                setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME)
            }
            is VGSTextInputType.CardExpDate -> {
                applyNewTextWatcher(ExpirationDateeTextwatcher)
                val filter = InputFilter.LengthFilter(inputType.length)
                filters = arrayOf(filter)
                setInputType(InputType.TYPE_CLASS_DATETIME)
            }
            is VGSTextInputType.InfoField -> {
                applyNewTextWatcher(null)
                filters = arrayOf()
                setInputType(InputType.TYPE_CLASS_TEXT)
            }
        }
    }

    fun getVGSInputType(): VGSTextInputType {
        return vgsInputType
    }

    fun getTextString():String {
        return if(vgsInputType is VGSTextInputType.CardNumber) {
            text?.replace(" ".toRegex(), "")?:""
        } else {
            text.toString()
        }
    }

    override fun setTag(tag: Any?) {
        tag?.run {
            super.setTag(tag)
            state.alias = this as String
        }
    }

    private fun applyNewTextWatcher(cardNumberTextWatcher: TextWatcher?) {
        activeTextWatcher?.let { removeTextChangedListener(activeTextWatcher) }
        cardNumberTextWatcher?.let { addTextChangedListener(cardNumberTextWatcher) }
        activeTextWatcher = cardNumberTextWatcher
    }

    internal fun setVGSPlaceHolderText(text:String?) {
        hint = text
        state.placeholder = text
        stateListener?.emit(id, state)
    }

    internal fun addDataViewStateChangeListener(listener: OnVgsViewStateChangeListener) {
        stateListener = listener

        stateListener!!.emit(id, state)
    }
}
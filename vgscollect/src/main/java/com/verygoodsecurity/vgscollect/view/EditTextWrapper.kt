package com.verygoodsecurity.vgscollect.view

import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText
import com.verygoodsecurity.vgscollect.view.text.validation.card.CardNumberTextWatcher
import com.verygoodsecurity.vgscollect.view.text.validation.card.ExpirationDateeTextwatcher

internal class EditTextWrapper(context: Context): TextInputEditText(context) {
    private var activeTextWatcher: TextWatcher? = null

    fun setInputFormatType(inputType: VGSTextInputType) {
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

    private fun applyNewTextWatcher(cardNumberTextWatcher: TextWatcher?) {
        activeTextWatcher?.let { removeTextChangedListener(activeTextWatcher) }
        cardNumberTextWatcher?.let { addTextChangedListener(cardNumberTextWatcher) }
        activeTextWatcher = cardNumberTextWatcher
    }
}
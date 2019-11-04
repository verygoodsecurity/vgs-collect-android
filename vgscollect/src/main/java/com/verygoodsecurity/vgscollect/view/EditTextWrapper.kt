package com.verygoodsecurity.vgscollect.view

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.text.InputFilter
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import androidx.core.view.ViewCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.model.VGSFieldState
import com.verygoodsecurity.vgscollect.view.text.validation.card.CVVValidateFilter
import com.verygoodsecurity.vgscollect.view.text.validation.card.CardNumberTextWatcher
import com.verygoodsecurity.vgscollect.view.text.validation.card.DateValiateFilter
import com.verygoodsecurity.vgscollect.view.text.validation.card.ExpirationDateeTextwatcher

internal class EditTextWrapper(context: Context): TextInputEditText(context) {

    private var vgsInputType: VGSTextInputType = VGSTextInputType.InfoField
    private val state = VGSFieldState()

    private var activeTextWatcher: TextWatcher? = null
    private var stateListener: OnVgsViewStateChangeListener? = null

    internal var isRequired:Boolean = true
        set(value) {
            field = value
            state.isRequired = value
            stateListener?.emit(id, state)
        }

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

    override fun setId(id: Int) {
        if(getId()== -1) {
            super.setId(id)
        }
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
                applyNewTextWatcher(null)
                val filterLength = InputFilter.LengthFilter(inputType.length)
                filters = arrayOf(CVVValidateFilter(), filterLength)
                setInputType(InputType.TYPE_CLASS_DATETIME)
            }
            is VGSTextInputType.CardOwnerName -> {
                applyNewTextWatcher(null)
                val filter = InputFilter.LengthFilter(inputType.length)
                filters = arrayOf(filter)
                setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME)
            }
            is VGSTextInputType.CardExpDate -> {
                applyNewTextWatcher(ExpirationDateeTextwatcher)
                val filterLength = InputFilter.LengthFilter(inputType.length)
                filters = arrayOf(filterLength)
                setInputType(InputType.TYPE_CLASS_DATETIME)
            }
            is VGSTextInputType.InfoField -> {
                applyNewTextWatcher(null)
                filters = arrayOf()
                setInputType(InputType.TYPE_CLASS_TEXT)
            }
        }
        state.type = vgsInputType
        stateListener?.emit(id, state)
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

    private fun applyNewTextWatcher(textWatcher: TextWatcher?) {
        activeTextWatcher?.let { removeTextChangedListener(activeTextWatcher) }
        textWatcher?.let { addTextChangedListener(textWatcher) }
        activeTextWatcher = textWatcher
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

    override fun onSaveInstanceState(): Parcelable? {
        val savedState = SavedState(super.onSaveInstanceState())
        savedState.isRequired = isRequired
        savedState.text = text.toString()
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            isRequired = state.isRequired
            setText(state.text)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    internal class SavedState : BaseSavedState {
        var isRequired: Boolean = false
        var text: CharSequence = ""

        companion object {
            @JvmField
            val CREATOR = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(source: Parcel): SavedState {
                    return SavedState(source)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }

        constructor(superState: Parcelable) : super(superState)

        constructor(`in`: Parcel) : super(`in`) {
            isRequired = `in`.readByte().toInt() != 0
            text = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(`in`);
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            TextUtils.writeToParcel(text, out, flags)
            out.writeByte((if (isRequired) 1 else 0).toByte())
        }
    }

}
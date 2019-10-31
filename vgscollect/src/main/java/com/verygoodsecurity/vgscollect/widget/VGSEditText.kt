package com.verygoodsecurity.vgscollect.widget

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.view.EditTextWrapper
import com.verygoodsecurity.vgscollect.view.VGSTextInputType

class VGSEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    internal val inputField: EditTextWrapper

    init {
        inputField = inflateInputField(context)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.VGSEditText,
            0, 0
        ).apply {

            try {
                val inputType =  when(getInteger(R.styleable.VGSEditText_inputType, -1)) {
                    0 -> VGSTextInputType.CardNumber
                    1 -> VGSTextInputType.CVVCardCode
                    2 -> VGSTextInputType.CardExpDate
                    3 -> VGSTextInputType.CardOwnerName
                    else -> VGSTextInputType.InfoField
                }
                inputField.setInputFormatType(inputType)

                val hint = getString(R.styleable.VGSEditText_hint)
                if(!hint.isNullOrBlank() ) {
                    inputField.hint = hint
                }
                val textSize:Float = getDimension(R.styleable.VGSEditText_textSize, -1f)
                if(textSize > 0) {
                    inputField.textSize = textSize
                }
                val textColor = getColor(R.styleable.VGSEditText_textColor, -1)
                if(textColor > 0) {
                    inputField.setTextColor(textColor)
                }

                val text = getString(R.styleable.VGSEditText_text)
                if(!text.isNullOrBlank()) {
                    inputField.setText(text)
                }
            } finally {
                recycle()
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if(parent !is VGSTextInputLauout) {
            setAddStatesFromChildren(true)
            addView(inputField)
        }
    }

    private fun inflateInputField(context: Context): EditTextWrapper {
        return EditTextWrapper(context)
    }

    override fun onDetachedFromWindow() {
        if(childCount > 0) removeAllViews()
        super.onDetachedFromWindow()
    }

    override fun addView(child: View?) {
        if(childCount == 0) {
            super.addView(child)
        }
    }






    fun getText(): Editable? {
        return inputField.text
    }

    fun setText( resId:Int) {
        inputField.setText(resId)
    }

    fun setText( resId:Int, type: TextView.BufferType) {
        inputField.setText(resId, type)
    }

    fun setText( text:CharSequence?) {
        inputField.setText(text)
    }

    fun setText( text:CharSequence?, type: TextView.BufferType) {
        inputField.setText(text, type)
    }

    fun setTextSize( size:Float ) {
        inputField.textSize = size
    }

    fun setTextSize( unit:Int, size:Float) {
        inputField.setTextSize(unit, size)
    }

    fun setTextColor(color:Int) {
        inputField.setTextColor(color)
    }
}
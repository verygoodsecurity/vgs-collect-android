package com.verygoodsecurity.vgscollect.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.view.EditTextWrapper
import com.verygoodsecurity.vgscollect.view.text.validation.card.VGSTextInputType

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
                    0 -> VGSTextInputType.CardNumber()
                    1 -> VGSTextInputType.CVVCardCode
                    2 -> VGSTextInputType.CardExpDate
                    3 -> VGSTextInputType.CardOwnerName
                    else -> VGSTextInputType.CardOwnerName
                }
                val tag = getString(R.styleable.VGSEditText_aliasName)
                val hint = getString(R.styleable.VGSEditText_hint)
                val textSize = getDimension(R.styleable.VGSEditText_textSize, 0f)
                val textColor = getColor(R.styleable.VGSEditText_textColor, 0)
                val text = getString(R.styleable.VGSEditText_text)
                val textStyle = getInt(R.styleable.VGSEditText_textStyle, -1)
                val cursorVisible = getBoolean(R.styleable.VGSEditText_cursorVisible, true)
                val isRequired = getBoolean(R.styleable.VGSEditText_isRequired, true)
                val singleLine = getBoolean(R.styleable.VGSEditText_singleLine, true)
                val scrollHorizontally = getBoolean(R.styleable.VGSEditText_scrollHorizontally, true)
                val gravity = getInt(R.styleable.VGSEditText_gravity, 0)
                val ellipsize = when(getInt(R.styleable.VGSEditText_ellipsize, 0)) {
                    1 -> TextUtils.TruncateAt.START
                    2 -> TextUtils.TruncateAt.MIDDLE
                    3 -> TextUtils.TruncateAt.END
                    4 -> TextUtils.TruncateAt.MARQUEE
                    else -> null
                }
                val minLines = getInt(R.styleable.VGSEditText_minLines, 0)
                val maxLines = getInt(R.styleable.VGSEditText_maxLines, 0)

                inputField.apply {
                    this.tag = tag
                    setInputFormatType(inputType)
                    setHint(hint)
                    setTextColor(textColor)
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
                    setText(text)
                    isCursorVisible = cursorVisible
                    setGravity(gravity)
                    setHorizontallyScrolling(scrollHorizontally)
                    setEllipsize(ellipsize)
                    setMaxLines(maxLines)
                    setMinLines(minLines)
                    setSingleLine(singleLine)
                    this.isRequired = isRequired

                    inputField.setTypeface(typeface, textStyle)
                }

            } finally {
                recycle()
            }
        }

    }

    fun setAliasName(alias:String) {
        inputField.tag = alias
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if(parent !is VGSTextInputLayout) {
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
        if(childCount == 0 && child is EditTextWrapper) {
            super.addView(child)
        }
    }

    fun setEllipsize(ellipsis: TextUtils.TruncateAt) {
        inputField.ellipsize = ellipsis
    }

    fun setMinLines(lines:Int) {
        inputField.minLines = lines
    }

    fun setMaxLines(lines:Int) {
        inputField.maxLines = lines
    }

    fun setSingleLine(singleLine:Boolean) {
        inputField.setSingleLine(singleLine)
    }

    override fun setFocusable(focusable: Boolean) {
        super.setFocusable(focusable)
        inputField.isFocusable = focusable
    }

    override fun setFocusableInTouchMode(focusableInTouchMode: Boolean) {
        super.setFocusableInTouchMode(focusableInTouchMode)
        inputField.isFocusableInTouchMode = focusableInTouchMode
    }

    override fun isEnabled() = inputField.isEnabled

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        inputField.isEnabled = enabled
    }

    fun setHint(text:String?) {
        inputField.hint = text
    }

    fun setHintTextColor(colors: ColorStateList) {
        inputField.setHintTextColor(colors)
    }

    fun setHintTextColor(color:Int) {
        inputField.setHintTextColor(color)
    }

    fun canScrollHorizontally(canScroll:Boolean) {
        inputField.setHorizontallyScrolling(canScroll)
    }

    fun setGravity(gravity:Int) {
        inputField.gravity = gravity
    }

    fun getGravity() = inputField.gravity

    fun setCursorVisible(isVisible:Boolean) {
        inputField.isCursorVisible = isVisible
    }

    fun setTextAppearance( context: Context, resId:Int) {
        inputField.setTextAppearance(context, resId)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun setTextAppearance(resId:Int) {
        inputField.setTextAppearance(resId)
    }

    fun setTypeface(typeface: Typeface) {
        inputField.typeface = typeface

    }

    fun setTypeface( tf:Typeface, style:Int) {
        inputField.setTypeface(tf, style)
    }

    fun setText( resId:Int) {
        inputField.setText(resId)
    }

    fun setText( resId:Int, type: TextView.BufferType) {
        inputField.setText(resId, type)
    }

    fun setText(text:CharSequence?) {
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

    fun setIsRequired(state:Boolean) {
        inputField.isRequired = state
    }

    override fun onSaveInstanceState(): Parcelable? {
        val savedState = SavedState(super.onSaveInstanceState())
        savedState.text = inputField.text.toString()
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            setText(state.text)
            super.onRestoreInstanceState(state.superState)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    internal class SavedState : BaseSavedState {
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
            text = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(`in`)
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            TextUtils.writeToParcel(text, out, flags)
        }
    }
}
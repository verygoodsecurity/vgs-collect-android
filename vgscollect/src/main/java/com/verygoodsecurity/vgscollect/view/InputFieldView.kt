package com.verygoodsecurity.vgscollect.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Parcel
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.view.card.CustomCardBrand
import com.verygoodsecurity.vgscollect.view.internal.EditTextWrapper
import com.verygoodsecurity.vgscollect.view.text.validation.card.FieldType

abstract class InputFieldView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val inputField = EditTextWrapper(context)
    private var isAttachPermitted = true

    override fun onDetachedFromWindow() {
        if(childCount > 0) removeAllViews()
        super.onDetachedFromWindow()
    }

    override fun addView(child: View?) {
        if(childCount == 0 && child is EditTextWrapper) {
            super.addView(child)
        }
    }

    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
        if(isAttachPermitted) {
            super.addView(child, params)
        }
    }

    override fun addView(child: View?, index: Int) {
        if(isAttachPermitted) {
            super.addView(child, index)
        }
    }

    override fun addView(child: View?, width: Int, height: Int) {
        if(isAttachPermitted) {
            super.addView(child, width, height)
        }
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        if(isAttachPermitted) {
            super.addView(child, index, params)
        }
    }

    override fun attachViewToParent(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        if(isAttachPermitted) {
            super.attachViewToParent(child, index, params)
        }
    }

    override fun addOnAttachStateChangeListener(listener: OnAttachStateChangeListener?) {
        if(isAttachPermitted) {
            super.addOnAttachStateChangeListener(listener)
        }
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        inputField.setPadding(left, top, right, bottom)
//        super.setPadding(left, top, right, bottom)
        super.setPadding(0, 0, 0, 0)
    }

    override fun getPaddingBottom(): Int {
        return if(isAttachPermitted) {
            super.getPaddingBottom()
        } else {
            inputField.paddingBottom
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun getPaddingEnd(): Int {
        return if(isAttachPermitted) {
            super.getPaddingEnd()
        } else {
            inputField.paddingEnd
        }
    }

    override fun getPaddingLeft(): Int {
        return if(isAttachPermitted) {
            super.getPaddingLeft()
        } else {
            inputField.paddingLeft
        }
    }

    override fun getPaddingRight(): Int {
        return if(isAttachPermitted) {
            super.getPaddingRight()
        } else {
            inputField.paddingRight
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun getPaddingStart(): Int {
        return if(isAttachPermitted) {
            super.getPaddingStart()
        } else {
            inputField.paddingStart
        }
    }

    override fun getPaddingTop(): Int {
        return if(isAttachPermitted) {
            super.getPaddingTop()
        } else {
            inputField.paddingTop
        }
    }

    private var bgDraw: Drawable? = null
    override fun onAttachedToWindow() {
        if(isAttachPermitted) {
            super.onAttachedToWindow()
            if (parent !is TextInputFieldLayout) {
                setAddStatesFromChildren(true)
                addView(inputField)
            }
            inputField.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
            bgDraw = background
            if(background != null) {
                setBackgroundColor(Color.TRANSPARENT)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    inputField.background = bgDraw
                } else {
                    inputField.setBackgroundDrawable(bgDraw)
                }
            }
            isAttachPermitted = false
        }
    }

    open fun setInputType(inputType: Int) {
        inputField.inputType = inputType
    }

    open fun setFieldName(fieldName:String?) {
        inputField.tag = fieldName
    }

    open fun setFieldName(resId:Int) {
        inputField.tag = resources.getString(resId, "")
    }

    open fun setEllipsize(type: Int) {
        val ellipsize = when(type) {
            1 -> TextUtils.TruncateAt.START
            2 -> TextUtils.TruncateAt.MIDDLE
            3 -> TextUtils.TruncateAt.END
            4 -> TextUtils.TruncateAt.MARQUEE
            else -> null
        }
        inputField.ellipsize = ellipsize
    }

    open fun setEllipsize(ellipsis: TextUtils.TruncateAt) {
        inputField.ellipsize = ellipsis
    }

    open fun setMinLines(lines:Int) {
        inputField.minLines = lines
    }

    open fun setMaxLines(lines:Int) {
        inputField.maxLines = lines
    }

    open fun setSingleLine(singleLine:Boolean) {
        inputField.setSingleLine(singleLine)
    }

    override fun setFocusableInTouchMode(focusableInTouchMode: Boolean) {
        super.setFocusableInTouchMode(focusableInTouchMode)
        inputField.isFocusableInTouchMode = focusableInTouchMode
    }

    open fun setHint(text:String?) {
        inputField.hint = text
    }

    open fun setHintTextColor(colors: ColorStateList) {
        inputField.setHintTextColor(colors)
    }

    open fun setHintTextColor(color:Int) {
        inputField.setHintTextColor(color)
    }

    open fun canScrollHorizontally(canScroll:Boolean) {
        inputField.setHorizontallyScrolling(canScroll)
    }

    open fun setGravity(gravity:Int) {
        inputField.gravity = gravity
    }

    open fun getGravity() = inputField.gravity

    open fun setCursorVisible(isVisible:Boolean) {
        inputField.isCursorVisible = isVisible
    }

    @Deprecated("deprecated")
    open fun setTextAppearance( context: Context, resId:Int) {
        inputField.setTextAppearance(context, resId)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    open fun setTextAppearance(resId:Int) {
        inputField.setTextAppearance(resId)
    }

    open fun getTypeface():Typeface? {
        return inputField.typeface
    }

    open fun setTypeface(typeface: Typeface) {
        inputField.typeface = typeface

    }

    open fun setTypeface(tf: Typeface, style:Int) {
        when(style) {
            0 -> inputField.typeface = Typeface.DEFAULT_BOLD
            1 -> inputField.setTypeface(tf, Typeface.BOLD)
            2 -> inputField.setTypeface(tf, Typeface.ITALIC)
        }
    }

    open fun setText( resId:Int) {
        inputField.setText(resId)
    }

    open fun setText( resId:Int, type: TextView.BufferType) {
        inputField.setText(resId, type)
    }

    open fun setText(text:CharSequence?) {
        inputField.setText(text)
    }

    open fun setText( text:CharSequence?, type: TextView.BufferType) {
        inputField.setText(text, type)
    }

    open fun setTextSize( size:Float ) {
        inputField.textSize = size
    }

    open fun setTextSize( unit:Int, size:Float) {
        inputField.setTextSize(unit, size)
    }

    open fun setTextColor(color:Int) {
        inputField.setTextColor(color)
    }

    open fun setIsRequired(state:Boolean) {
        inputField.isRequired = state
    }

    open fun setFieldType(type:FieldType) {
        inputField.setFieldType(type)
    }

    open fun setCursorColor(color:Int) {
        inputField.setCursorDrawableColor(color)
    }

    internal fun addStateListener(stateListener: OnVgsViewStateChangeListener) {
        inputField.stateListener = stateListener
    }

    internal fun getEditTextWrapper(): EditTextWrapper {
        return inputField
    }

    protected fun applyCardIconGravity(gravity:Int) {
        inputField.setCardPreviewIconGravity(gravity)
    }

    protected fun applyCardBrand(c: CustomCardBrand) {
        inputField.setCardBrand(c)
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

    protected class SavedState : BaseSavedState {
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

        constructor(superState: Parcelable?) : super(superState)

        constructor(`in`: Parcel) : super(`in`) {
            text = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(`in`)
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            TextUtils.writeToParcel(text, out, flags)
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        inputField.isEnabled = enabled
    }
}
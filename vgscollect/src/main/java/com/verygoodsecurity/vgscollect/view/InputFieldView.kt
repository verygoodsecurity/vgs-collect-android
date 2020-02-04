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
import com.verygoodsecurity.vgscollect.core.storage.DependencyListener
import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.view.card.CustomCardBrand
import com.verygoodsecurity.vgscollect.view.internal.InputField
import com.verygoodsecurity.vgscollect.view.card.FieldType

/**
 * An abstract class that provide displays text user-editable text to the user.
 */
abstract class InputFieldView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val inputField:InputField? = InputField(context)
    private var isAttachPermitted = true

    internal val notifier = DependencyNotifier(inputField!!)

    /**
     * Delegate class that helps deliver new requirements between related input fields.
     *
     * @param notifier The listener that emits new dependencies for apply.
     */
    class DependencyNotifier(notifier: DependencyListener) : DependencyListener by notifier

    override fun onDetachedFromWindow() {
        if(childCount > 0) removeAllViews()
        super.onDetachedFromWindow()
    }

    override fun addView(child: View?) {
        if(childCount == 0 && child is InputField) {
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
        inputField?.setPadding(left, top, right, bottom)
        super.setPadding(0, 0, 0, 0)
    }

    override fun getPaddingBottom(): Int {
        return if(isAttachPermitted) {
            super.getPaddingBottom()
        } else {
            inputField?.paddingBottom?:0
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun getPaddingEnd(): Int {
        return if(isAttachPermitted) {
            super.getPaddingEnd()
        } else {
            inputField?.paddingEnd?:0
        }
    }

    override fun getPaddingLeft(): Int {
        return if(isAttachPermitted) {
            super.getPaddingLeft()
        } else {
            inputField?.paddingLeft?:0
        }
    }

    override fun getPaddingRight(): Int {
        return if(isAttachPermitted) {
            super.getPaddingRight()
        } else {
            inputField?.paddingRight?:0
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun getPaddingStart(): Int {
        return if(isAttachPermitted) {
            super.getPaddingStart()
        } else {
            inputField?.paddingStart?:0
        }
    }

    override fun getPaddingTop(): Int {
        return if(isAttachPermitted) {
            super.getPaddingTop()
        } else {
            inputField?.paddingTop?:0
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
            inputField?.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
            bgDraw = background
            if(background != null) {
                setBackgroundColor(Color.TRANSPARENT)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    inputField?.background = bgDraw
                } else {
                    inputField?.setBackgroundDrawable(bgDraw)
                }
            }
            isAttachPermitted = false
        }
    }

    /**
     * Set the type of the content with a constant as defined for input field.
     *
     * @param inputType
     */
    open fun setInputType(inputType: Int) {
        inputField?.inputType = inputType
    }

    /**
     * Sets the text to be used for data transfer to VGS proxy. Usually,
     * it is similar to field-name in JSON path in your inbound route filters.
     *
     * @param fieldName the name of the field
     */
    open fun setFieldName(fieldName:String?) {
        inputField?.tag = fieldName
    }

    /**
     * Sets the text to be used for data transfer to VGS proxy. Usually,
     * it is similar to field-name in JSON path in your inbound route filters.
     *
     * @param resId the resource identifier of the field name
     */
    open fun setFieldName(resId:Int) {
        inputField?.tag = resources.getString(resId, "")
    }

    /**
     *
     */
    open fun getFieldName():String? = inputField?.tag as String?

    /**
     * Causes words in the text that are longer than the view's width to be ellipsized
     * instead of broken in the middle.
     *
     * @param type integer value of TextUtils.TruncateAt
     */
    open fun setEllipsize(type: Int) {
        val ellipsize = when(type) {
            1 -> TextUtils.TruncateAt.START
            2 -> TextUtils.TruncateAt.MIDDLE
            3 -> TextUtils.TruncateAt.END
            4 -> TextUtils.TruncateAt.MARQUEE
            else -> null
        }
        inputField?.ellipsize = ellipsize
    }

    /**
     * Causes words in the text that are longer than the view's width to be ellipsized
     * instead of broken in the middle.
     *
     * @param ellipsis
     */
    open fun setEllipsize(ellipsis: TextUtils.TruncateAt) {
        inputField?.ellipsize = ellipsis
    }

    /**
     * Sets the height of the TextView to be at least minLines tall.
     *
     * @param lines the minimum height of TextView in terms of number of lines
     */
    open fun setMinLines(lines:Int) {
        inputField?.minLines = lines
    }

    /**
     * Sets the height of the TextView to be at most maxLines tall.
     *
     * @param lines the maximum height of TextView in terms of number of lines.
     */
    open fun setMaxLines(lines:Int) {
        inputField?.maxLines = lines
    }

    /**
     * If true, sets the properties of this field
     * (number of lines, horizontally scrolling, transformation method) to be for a single-line input.
     *
     * @param singleLine
     */
    open fun setSingleLine(singleLine:Boolean) {
        inputField?.setSingleLine(singleLine)
    }

    override fun setFocusableInTouchMode(focusableInTouchMode: Boolean) {
        super.setFocusableInTouchMode(focusableInTouchMode)
        inputField?.isFocusableInTouchMode = focusableInTouchMode
    }

    /**
     * Sets the text to be displayed when the text of the TextView is empty.
     * Null means to use the normal empty text. The hint does not currently participate
     * in determining the size of the view.
     *
     * @param text
     */
    open fun setHint(text:String?) {
        inputField?.hint = text
    }

    /**
     * Sets the color of the hint text.
     *
     * @param colors
     */
    open fun setHintTextColor(colors: ColorStateList) {
        inputField?.setHintTextColor(colors)
    }

    /**
     * Sets the color of the hint text for all the states (disabled, focussed, selected...)
     * of this TextView.
     *
     * @param color
     */
    open fun setHintTextColor(color:Int) {
        inputField?.setHintTextColor(color)
    }

    /**
     *
     */
    open fun canScrollHorizontally(canScroll:Boolean) {
        inputField?.setHorizontallyScrolling(canScroll)
    }

    /**
     * Sets the horizontal alignment of the text and the vertical gravity that will be used when
     * there is extra space in the TextView beyond what is required for the text itself.
     *
     * @param gravity
     */
    open fun setGravity(gravity:Int) {
        inputField?.gravity = gravity
    }

    /**
     * Returns the horizontal and vertical alignment of this TextView.
     *
     * @return current gravity
     */
    open fun getGravity() = inputField?.gravity

    /**
     * Set whether the cursor is visible.
     *
     * @param isVisible
     */
    open fun setCursorVisible(isVisible:Boolean) {
        inputField?.isCursorVisible = isVisible
    }

    /**
     * Sets the text color, size, style, hint color, and highlight color from the specified
     * TextAppearance resource.
     *
     * @param context
     * @param resId the resource identifier of the style to apply
     */
    @Deprecated("deprecated")
    open fun setTextAppearance( context: Context, resId:Int) {
        inputField?.setTextAppearance(context, resId)
    }

    /**
     * Sets the text appearance from the specified style resource.
     *
     * @param resId the resource identifier of the style to apply
     */
    @RequiresApi(Build.VERSION_CODES.M)
    open fun setTextAppearance(resId:Int) {
        inputField?.setTextAppearance(resId)
    }

    /**
     * Gets the current Typeface that is used to style the text.
     *
     * @return The current Typeface.
     */
    open fun getTypeface():Typeface? {
        return inputField?.typeface
    }

    /**
     * Sets the typeface and style in which the text should be displayed.
     *
     * @param typeface This value may be null.
     */
    open fun setTypeface(typeface: Typeface) {
        inputField?.typeface = typeface
    }

    /**
     * Sets the typeface and style in which the text should be displayed,
     * and turns on the fake bold and italic bits in the Paint if the Typeface
     * that you provided does not have all the bits in the style that you specified.
     *
     * @param tf This value may be null.
     * @param style Value is Typeface.NORMAL, Typeface.BOLD, Typeface.ITALIC, or Typeface.BOLD_ITALIC
     */
    open fun setTypeface(tf: Typeface, style:Int) {
        when(style) {
            0 -> inputField?.typeface = Typeface.DEFAULT_BOLD
            1 -> inputField?.setTypeface(tf, Typeface.BOLD)
            2 -> inputField?.setTypeface(tf, Typeface.ITALIC)
        }
    }

    /**
     * Sets the text to be displayed using a string resource identifier.
     *
     * @param resId the resource identifier of the string resource to be displayed
     */
    open fun setText( resId:Int) {
        inputField?.setText(resId)
    }

    /**
     * Sets the text to be displayed using a string resource identifier and the TextView.BufferType.
     *
     * @param resId the resource identifier of the string resource to be displayed
     * @param type a TextView.BufferType which defines whether the text is stored as a static text,
     * styleable/spannable text, or editable text
     */
    open fun setText( resId:Int, type: TextView.BufferType) {
        inputField?.setText(resId, type)
    }

    /**
     * Sets the text to be displayed.
     *
     * @param text text to be displayed
     */
    open fun setText(text:CharSequence?) {
        inputField?.setText(text)
    }

    /**
     * Sets the text to be displayed and the TextView.BufferType.
     *
     * @param text text to be displayed
     * @param type a TextView.BufferType which defines whether the text is stored as a static text,
     * styleable/spannable text, or editable text
     *
     * @see TextView.BufferType
     */
    open fun setText( text:CharSequence?, type: TextView.BufferType) {
        inputField?.setText(text, type)
    }

    /**
     * Set the default text size to the given value, interpreted as "scaled pixel" units.
     * This size is adjusted based on the current density and user font size preference.
     *
     * @param size The scaled pixel size.
     */
    open fun setTextSize( size:Float ) {
        inputField?.textSize = size
    }

    /**
     * Set the default text size to a given unit and value.
     * See TypedValue for the possible dimension units.
     *
     * @param unit The desired dimension unit.
     * @param size The desired size in the given units.
     */
    open fun setTextSize( unit:Int, size:Float) {
        inputField?.setTextSize(unit, size)
    }

    /**
     * Sets the text color for all the states (normal, selected, focused) to be this color.
     *
     * @param color A color value that will be applied
     */
    open fun setTextColor(color:Int) {
        inputField?.setTextColor(color)
    }

    /**
     * Specifies whether the text inside input field is required to be filled before sending.
     *
     * @param state Set true if the input required.
     */
    open fun setIsRequired(state:Boolean) {
        inputField?.isRequired = state
    }

    /**
     * Gets the current field type of the InputFieldView.
     *
     * @return FieldType
     *
     * @see FieldType
     */
    open fun getFieldType():FieldType {
        return inputField?.fieldType?:FieldType.INFO
    }

    /**
     * Sets type of current input field.
     * Choosing the input type you configure the limitations for this type.
     *
     * @param type The type of current input field.
     *
     * @see FieldType
     */
    open fun setFieldType(type: FieldType) {
        inputField?.fieldType = type
    }

    /**
     * Sets the color of the input field cursor.
     *
     * @param color The cursor color.
     */
    open fun setCursorColor(color:Int) {
        inputField?.setCursorDrawableColor(color)
    }

    internal fun addStateListener(stateListener: OnVgsViewStateChangeListener) {
        inputField?.stateListener = stateListener
    }

    internal fun getEditTextWrapper(): InputField? {
        return inputField
    }

    protected fun applyCardIconGravity(gravity:Int) {
        inputField?.setCardPreviewIconGravity(gravity)
    }

    protected fun applyCardBrand(c: CustomCardBrand) {
        inputField?.setCardBrand(c)
    }

    protected fun setNumberDivider(divider:String?) {
        inputField?.setNumberDivider(divider)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val savedState = SavedState(super.onSaveInstanceState())
        savedState.text = inputField?.text.toString()
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
        inputField?.isEnabled = enabled
    }
}
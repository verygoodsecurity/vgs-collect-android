package com.verygoodsecurity.vgscollect.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.storage.DependencyListener
import com.verygoodsecurity.vgscollect.view.card.CustomCardBrand
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.date.DatePickerMode
import com.verygoodsecurity.vgscollect.view.internal.BaseInputField
import com.verygoodsecurity.vgscollect.view.internal.CardInputField
import com.verygoodsecurity.vgscollect.view.internal.DateInputField
import com.verygoodsecurity.vgscollect.view.internal.InputField
import com.verygoodsecurity.vgscollect.view.material.TextInputFieldLayout

/**
 * An abstract class that provide displays text user-editable text to the user.
 */
abstract class InputFieldView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), AccessibilityStatePreparer {

    private var isAttachPermitted = true

    private lateinit var notifier:DependencyNotifier
    private var imeOptions:Int = 0

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.InputFieldView,
            0, 0
        ).apply {
            try {
                imeOptions = getInt(R.styleable.InputFieldView_imeOptions, EditorInfo.IME_ACTION_DONE)
            } finally {
                recycle()
            }
        }
    }

    /**
     * Delegate class that helps deliver new requirements between related input fields.
     *
     * @param notifier The listener that emits new dependencies for apply.
     */
    internal class DependencyNotifier(notifier: DependencyListener) : DependencyListener by notifier

    private lateinit var fieldType:FieldType

    private lateinit var inputField: BaseInputField

    protected fun setupViewType(type:FieldType) {
        with(type) {
            fieldType = this
            inputField = BaseInputField.getInputField(context, this)
            syncInputState()
        }
    }

    override fun getView():View {
        return inputField
    }

    override fun getDependencyListener(): DependencyListener = notifier

    override fun onDetachedFromWindow() {
        if(childCount > 0) removeAllViews()
        super.onDetachedFromWindow()
    }

    override fun addView(child: View?) {
        if(childCount == 0 && child is BaseInputField) {
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

    /**
     * Sets the padding.
     * The view may add on the space required to display the scrollbars, depending on the style and visibility of the scrollbars.
     * So the values returned from getPaddingLeft(), getPaddingTop(), getPaddingRight() and getPaddingBottom()
     * may be different from the values set in this call.
     *
     * @param left the left padding in pixels
     * @param top the top padding in pixels
     * @param right the right padding in pixels
     * @param bottom the bottom padding in pixels
     */
    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        if(::inputField.isInitialized) {
            inputField.setPadding(left, top, right, bottom)
        }
        super.setPadding(0, 0, 0, 0)
    }

    /**
     * Returns the bottom padding of this view.
     * If there are inset and enabled scrollbars, this value may include the space required to display the scrollbars as well.
     *
     * @return the bottom padding in pixels
     */
    override fun getPaddingBottom(): Int {
        return if(isAttachPermitted) {
            super.getPaddingBottom()
        } else {
            inputField.paddingBottom
        }
    }

    /**
     * Returns the end padding of this view depending on its resolved layout direction.
     * If there are inset and enabled scrollbars, this value may include the space required to display the scrollbars as well.
     *
     * @return the end padding in pixels
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun getPaddingEnd(): Int {
        return if(isAttachPermitted) {
            super.getPaddingEnd()
        } else {
            inputField.paddingEnd
        }
    }

    /**
     * Returns the left padding of this view.
     * If there are inset and enabled scrollbars, this value may include the space required to display the scrollbars as well.
     *
     * @return the left padding in pixels
     */
    override fun getPaddingLeft(): Int {
        return if(isAttachPermitted) {
            super.getPaddingLeft()
        } else {
            inputField.paddingLeft
        }
    }

    /**
     * Returns the right padding of this view.
     * If there are inset and enabled scrollbars, this value may include the space required to display the scrollbars as well.
     *
     * @return the right padding in pixels
     */
    override fun getPaddingRight(): Int {
        return if(isAttachPermitted) {
            super.getPaddingRight()
        } else {
            inputField.paddingRight
        }
    }

    /**
     * Returns the start padding of this view depending on its resolved layout direction.
     * If there are inset and enabled scrollbars, this value may include the space required to display the scrollbars as well.
     *
     * @return the start padding in pixels
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun getPaddingStart(): Int {
        return if(isAttachPermitted) {
            super.getPaddingStart()
        } else {
            inputField.paddingStart
        }
    }

    /**
     * Returns the top padding of this view.
     *
     * @return the top padding in pixels
     */
    override fun getPaddingTop(): Int {
        return if(isAttachPermitted) {
            super.getPaddingTop()
        } else {
            inputField.paddingTop
        }
    }

    private var bgDraw: Drawable? = null
    public override fun onAttachedToWindow() {
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

    /**
     * Get the type of the editable content.
     *
     * @return inputType
     */
    open fun getInputType(): Int {
        return inputField.inputType
    }

    /**
     * Set the type of the content with a constant as defined for input field.
     *
     * @param inputType
     */
    open fun setInputType(inputType: Int) {
        inputField.inputType = inputType
    }

    /**
     * Sets the text to be used for data transfer to VGS proxy. Usually,
     * it is similar to field-name in JSON path in your inbound route filters.
     *
     * @param fieldName the name of the field
     */
    open fun setFieldName(fieldName:String?) {
        inputField.tag = fieldName
    }

    /**
     * Return the text that field is using for data transfer to VGS proxy.
     *
     * @return The text used by the field.
     */
    open fun getFieldName():String? = inputField.tag as String?

    /**
     * Sets the text to be used for data transfer to VGS proxy. Usually,
     * it is similar to field-name in JSON path in your inbound route filters.
     *
     * @param resId the resource identifier of the field name
     */
    open fun setFieldName(resId:Int) {
        inputField.tag = resources.getString(resId, "")
    }

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
        inputField.ellipsize = ellipsize
    }

    /**
     * Causes words in the text that are longer than the view's width to be ellipsized
     * instead of broken in the middle.
     *
     * @param ellipsis
     */
    open fun setEllipsize(ellipsis: TextUtils.TruncateAt) {
        inputField.ellipsize = ellipsis
    }

    /**
     * Sets the height of the TextView to be at least minLines tall.
     *
     * @param lines the minimum height of TextView in terms of number of lines
     */
    open fun setMinLines(lines:Int) {
        inputField.minLines = lines
    }

    /**
     * Sets the height of the TextView to be at most maxLines tall.
     *
     * @param lines the maximum height of TextView in terms of number of lines.
     */
    open fun setMaxLines(lines:Int) {
        inputField.maxLines = lines
    }

    /**
     * If true, sets the properties of this field
     * (number of lines, horizontally scrolling, transformation method) to be for a single-line input.
     *
     * @param singleLine
     */
    open fun setSingleLine(singleLine:Boolean) {
        inputField.isSingleLine = singleLine
    }

    override fun setFocusableInTouchMode(focusableInTouchMode: Boolean) {
        super.setFocusableInTouchMode(focusableInTouchMode)
        inputField.isFocusableInTouchMode = focusableInTouchMode
    }

    /**
     * Sets the text to be displayed when the text of the TextView is empty.
     * Null means to use the normal empty text. The hint does not currently participate
     * in determining the size of the view.
     *
     * @param text
     */
    open fun setHint(text:String?) {
        inputField.hint = text
    }

    /**
     * Sets the color of the hint text.
     *
     * @param colors
     */
    open fun setHintTextColor(colors: ColorStateList) {
        inputField.setHintTextColor(colors)
    }

    /**
     * Sets the color of the hint text for all the states (disabled, focussed, selected...)
     * of this TextView.
     *
     * @param color
     */
    open fun setHintTextColor(color:Int) {
        inputField.setHintTextColor(color)
    }

    /**
     * Sets whether the text should be allowed to be wider than the View is. If false,
     * it will be wrapped to the width of the View.
     *
     * @param canScroll
     */
    open fun canScrollHorizontally(canScroll:Boolean) {
        inputField.setHorizontallyScrolling(canScroll)
    }

    /**
     * Sets the horizontal alignment of the text and the vertical gravity that will be used when
     * there is extra space in the TextView beyond what is required for the text itself.
     *
     * @param gravity
     */
    open fun setGravity(gravity:Int) {
        inputField.gravity = gravity
    }

    /**
     * Returns the horizontal and vertical alignment of this TextView.
     *
     * @return current gravity
     */
    open fun getGravity() = inputField.gravity

    /**
     * Set whether the cursor is visible.
     *
     * @param isVisible
     */
    open fun setCursorVisible(isVisible:Boolean) {
        inputField.isCursorVisible = isVisible
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
        inputField.setTextAppearance(context, resId)
    }

    /**
     * Sets the text appearance from the specified style resource.
     *
     * @param resId the resource identifier of the style to apply
     */
    @RequiresApi(Build.VERSION_CODES.M)
    open fun setTextAppearance(resId:Int) {
        inputField.setTextAppearance(resId)
    }

    /**
     * Gets the current Typeface that is used to style the text.
     *
     * @return The current Typeface.
     */
    open fun getTypeface():Typeface? {
        return inputField.typeface
    }

    /**
     * Sets the typeface and style in which the text should be displayed.
     *
     * @param typeface This value may be null.
     */
    open fun setTypeface(typeface: Typeface) {
        inputField.typeface = typeface
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
            0 -> inputField.typeface = Typeface.DEFAULT_BOLD
            1 -> inputField.setTypeface(tf, Typeface.BOLD)
            2 -> inputField.setTypeface(tf, Typeface.ITALIC)
        }
    }

    /**
     * Sets the text to be displayed using a string resource identifier.
     *
     * @param resId the resource identifier of the string resource to be displayed
     */
    open fun setText( resId:Int) {
        inputField.setText(resId)
    }

    /**
     * Sets the text to be displayed using a string resource identifier and the TextView.BufferType.
     *
     * @param resId the resource identifier of the string resource to be displayed
     * @param type a TextView.BufferType which defines whether the text is stored as a static text,
     * styleable/spannable text, or editable text
     */
    open fun setText( resId:Int, type: TextView.BufferType) {
        inputField.setText(resId, type)
    }

    /**
     * Sets the text to be displayed.
     *
     * @param text text to be displayed
     */
    open fun setText(text:CharSequence?) {
        inputField.setText(text)
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
        inputField.setText(text, type)
    }

    /**
     * Set the default text size to the given value, interpreted as "scaled pixel" units.
     * This size is adjusted based on the current density and user font size preference.
     *
     * @param size The scaled pixel size.
     */
    open fun setTextSize( size:Float ) {
        inputField.textSize = size
    }

    /**
     * Set the default text size to a given unit and value.
     * See TypedValue for the possible dimension units.
     *
     * @param unit The desired dimension unit.
     * @param size The desired size in the given units.
     */
    open fun setTextSize( unit:Int, size:Float) {
        inputField.setTextSize(unit, size)
    }

    /**
     * Sets the text color for all the states (normal, selected, focused) to be this color.
     *
     * @param color A color value that will be applied
     */
    open fun setTextColor(color:Int) {
        inputField.setTextColor(color)
    }

    /**
     * Specifies whether the text inside input field is required to be filled before sending.
     *
     * @param state Set true if the input required.
     */
    open fun setIsRequired(state:Boolean) {
        inputField.isRequired = state
    }

    /**
     * Specifies whether the text inside input field is required to be filled before sending.
     * If the field doesn't require, then it may be sent to the server as empty.
     *
     * @return true if the input required.
     */
    open fun isRequired():Boolean {
        return inputField.isRequired
    }

    /**
     * Gets the current field type of the InputFieldView.
     *
     * @return FieldType
     *
     * @see FieldType
     */
    fun getFieldType():FieldType {
        return fieldType
    }

    /**
     * Sets type of current input field.
     * Choosing the input type you configure the limitations for this type.
     *
     * @param type The type of current input field.
     *
     * @see FieldType
     */
    @Deprecated("deprecated from 1.0.5")
    fun applyFieldType(type: FieldType) {
        fieldType = type
        if(::notifier.isInitialized.not()) {
            inputField = InputField(context)
            syncInputState()
        }
        (inputField as? InputField)?.setType(type)
    }

    private fun syncInputState() {
        notifier = DependencyNotifier(inputField)

        inputField.nextFocusDownId = nextFocusDownId
        inputField.nextFocusForwardId = nextFocusForwardId
        inputField.nextFocusUpId = nextFocusUpId
        inputField.nextFocusLeftId = nextFocusLeftId
        inputField.nextFocusRightId = nextFocusRightId
        inputField.imeOptions = imeOptions
    }

    internal fun addStateListener(stateListener: OnVgsViewStateChangeListener) {
        inputField.stateListener = stateListener
    }

    protected fun applyCardIconGravity(gravity:Int) {
        if(fieldType == FieldType.CARD_NUMBER) {
            (inputField as? CardInputField)?.setCardPreviewIconGravity(gravity)
        }
    }

    protected fun getCardIconGravity():Int {
        return if(fieldType == FieldType.CARD_NUMBER) {
            (inputField as? CardInputField)?.getCardPreviewIconGravity()?:-1
        } else {
            -1
        }
    }

    protected fun applyCardBrand(c: CustomCardBrand) {
        if(fieldType == FieldType.CARD_NUMBER) {
            (inputField as? CardInputField)?.setCardBrand(c)
        }
    }

    protected fun setNumberDivider(divider:String?) {
        if(fieldType == FieldType.CARD_NUMBER) {
            (inputField as? CardInputField)?.setNumberDivider(divider)
        }
    }

    protected fun getNumberDivider(): Char? {
        return if(fieldType == FieldType.CARD_NUMBER) {
            (inputField as? CardInputField)?.getNumberDivider()?.first()
        } else {
            null
        }
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

    protected fun setDatePattern(pattern:String?) {
        if(fieldType == FieldType.CARD_EXPIRATION_DATE) {
            (inputField as? DateInputField)?.setDatePattern(pattern)
        }
    }

    protected fun getDatePattern():String? {
        return (inputField as? DateInputField)?.getDatePattern()
    }

    protected fun setDatePickerMode(type:Int) {
        if(fieldType == FieldType.CARD_EXPIRATION_DATE) {
            (inputField as? DateInputField)?.setDatePickerMode(type)
        }
    }

    protected fun getDateMode(): DatePickerMode? {
        return (inputField as? DateInputField)?.getDatePickerMode()
    }

    protected fun maxDate(date:String) {
        if(fieldType == FieldType.CARD_EXPIRATION_DATE) {
            (inputField as? DateInputField)?.setMaxDate(date)
        }
    }
    protected fun minDate(date:String) {
        if(fieldType == FieldType.CARD_EXPIRATION_DATE) {
            (inputField as? DateInputField)?.setMinDate(date)
        }
    }
    protected fun setMinDate(date:Long) {
        if(fieldType == FieldType.CARD_EXPIRATION_DATE) {
            (inputField as? DateInputField)?.setMinDate(date)
        }
    }

    override fun setNextFocusForwardId(nextFocusForwardId: Int) {
        inputField.nextFocusForwardId = nextFocusForwardId
        super.setNextFocusForwardId(nextFocusForwardId)
    }

    override fun setNextFocusLeftId(nextFocusLeftId: Int) {
        inputField.nextFocusLeftId = nextFocusLeftId
        super.setNextFocusLeftId(nextFocusLeftId)
    }

    override fun setNextFocusRightId(nextFocusRightId: Int) {
        inputField.nextFocusRightId = nextFocusRightId
        super.setNextFocusRightId(nextFocusRightId)
    }

    override fun setNextFocusUpId(nextFocusUpId: Int) {
        inputField.nextFocusUpId = nextFocusUpId
        super.setNextFocusUpId(nextFocusUpId)
    }

    override fun setNextFocusDownId(nextFocusDownId: Int) {
        inputField.nextFocusDownId = nextFocusDownId
        super.setNextFocusDownId(nextFocusDownId)
    }

    override fun requestFocus(direction: Int, previouslyFocusedRect: Rect?): Boolean {
        return inputField.requestFocus(direction, previouslyFocusedRect)
    }

    override fun clearFocus() {
        inputField.clearFocus()
    }

    /**
     * Change the editor type integer associated with the text view, which
     * is reported to an Input Method Editor when it has focus.
     */
    fun setImeOptions(imeOptions:Int) {
        inputField.imeOptions = imeOptions
    }

    fun getImeOptions():Int {
        return inputField.imeOptions
    }

}
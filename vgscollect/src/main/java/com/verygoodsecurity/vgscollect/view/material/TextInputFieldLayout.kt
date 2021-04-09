package com.verygoodsecurity.vgscollect.view.material

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.annotation.StyleRes
import androidx.annotation.VisibleForTesting
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.material.internal.InputLayoutStateImpl
import com.verygoodsecurity.vgscollect.view.material.internal.TextInputLayoutWrapper
import com.verygoodsecurity.vgscollect.widget.VGSTextInputLayout

/**
 * An abstract class that provide floating label when
 * the hint is hidden due to user inputting text.
 *
 * @since 1.0.0
 */
abstract class TextInputFieldLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var isAttachPermitted = true

    private val fieldState: InputLayoutStateImpl

    init {
        val textInputLayout = TextInputLayoutWrapper(
            context
        ).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }
        fieldState = InputLayoutStateImpl(textInputLayout)

        addView(textInputLayout)
    }

    /**
     * Sets the padding. The view may add on the space required to display
     * the scrollbars, depending on the style and visibility of the scrollbars.
     * So the values returned from getPaddingLeft, getPaddingTop,
     * getPaddingRight and getPaddingBottom may be different
     * from the values set in this call.
     */
    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        if(fieldState != null) {
            fieldState.left = left
            fieldState.top = top
            fieldState.right = right
            fieldState.bottom = bottom
        }
        super.setPadding(0,0,0,0)
    }

    /**
     * Returns the bottom padding of this view. If there are inset and enabled
     * scrollbars, this value may include the space required to display the
     * scrollbars as well.
     *
     * @return the bottom padding in pixels
     */
    override fun getPaddingBottom(): Int {
        return fieldState.bottom
    }

    /**
     * Returns the end padding of this view depending on its resolved layout direction.
     * If there are inset and enabled scrollbars, this value may include the space
     * required to display the scrollbars as well.
     *
     * @return the end padding in pixels
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun getPaddingEnd(): Int {
        return fieldState.end
    }

    /**
     * Returns the left padding of this view. If there are inset and enabled
     * scrollbars, this value may include the space required to display the
     * scrollbars as well.
     *
     * @return the left padding in pixels
     */
    override fun getPaddingLeft(): Int {
        return fieldState.left
    }

    /**
     * Returns the right padding of this view. If there are inset and enabled
     * scrollbars, this value may include the space required to display the
     * scrollbars as well.
     *
     * @return the right padding in pixels
     */
    override fun getPaddingRight(): Int {
        return fieldState.right
    }

    /**
     * Returns the start padding of this view depending on its resolved layout direction.
     * If there are inset and enabled scrollbars, this value may include the space
     * required to display the scrollbars as well.
     *
     * @return the start padding in pixels
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun getPaddingStart(): Int {
        return fieldState.start
    }

    /**
     * Returns the top padding of this view.
     *
     * @return the top padding in pixels
     */
    override fun getPaddingTop(): Int {
        return fieldState.top
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

    override fun addView(child: View?) {
        val v = handleNewChild(child)
        super.addView(v)
        fieldState.refresh()
    }

    override fun addView(child: View?, index: Int) {
        handleNewChild(child)?.let { v ->
            super.addView(v, index)
        }
    }

    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
        handleNewChild(child)?.let { v ->
            super.addView(v, params)
        }
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        handleNewChild(child)?.let { v ->
            super.addView(v, index, params)
        }
    }

    override fun addView(child: View?, width: Int, height: Int) {
        handleNewChild(child)?.let { v ->
            super.addView(v, width, height)
        }
    }

    override fun onAttachedToWindow() {
        if(isAttachPermitted) {
            super.onAttachedToWindow()
            isAttachPermitted = false
        }
    }

    private fun handleNewChild(child: View?): View? {
        return child?.run {
            when(this) {
                is TextInputLayoutWrapper -> this
                is InputFieldView -> {
                    val editText = child as InputFieldView
                    attachViewToParent(editText, childCount, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))

                    fieldState.addChildView(child)
                    null
                }
                else -> null
            }
        }
    }

    /**
     * Whether the error functionality is enabled or not in this layout. Enabling this functionality
     * before setting an error message via {@link #setError(CharSequence)}, will mean that this layout
     * will not change size when an error is displayed.
     *
     * @param isEnabled true if layout should reserve place for error message, false otherwise.
     */
    open fun setErrorEnabled(isEnabled: Boolean) {
        fieldState.isErrorEnabled = isEnabled
    }

    /**
     * Sets an error message that will be displayed below our EditText. If the error
     * is null, the error message will be cleared.
     *
     * <p>If the error functionality has not been enabled via setErrorEnabled(), then
     * it will be automatically enabled if {@code error} is not empty.
     *
     * @param errorText Error message to display, or null to clear
     * @see #getError()
     */
    open fun setError(errorText:CharSequence?) {
        fieldState.error = errorText
    }

    /**
     * Sets an error message that will be displayed below our EditText. If the error
     * is null, the error message will be cleared.
     *
     * <p>If the error functionality has not been enabled via setErrorEnabled(), then
     * it will be automatically enabled if {@code error} is not empty.
     *
     * @param errorText Error messageResId to display, or null to clear
     * @see #getError()
     */
    open fun setError(resId:Int) {
        fieldState.error = context.resources.getString(resId)
    }

    /**
     * Returns the error message that was set to be displayed with setError(CharSequence), or
     * null if no error was set or if error displaying is not enabled.
     */
    fun getError():String? {
        return fieldState.error?.toString()
    }

    /**
     * Returns the hint that is displayed when the text of the TextView
     * is empty.
     */
    open fun getHint() = fieldState.hint

    /**
     * Sets the text to be displayed when the text of the TextView is empty.
     * Null means to use the normal empty text. The hint does not currently
     * participate in determining the size of the view.
     */
    open fun setHint(text:String?) {
        fieldState.hint = text
    }

    /**
     * Sets the text to be displayed when the text of the TextView is empty,
     * from a resource.
     */
    open fun setHint(resId:Int) {
        fieldState.hint = context.resources.getString(resId)
    }

    /**
     * Enables or disable the password visibility toggle functionality.
     *
     * <p>When enabled, a button is placed at the end of the EditText which enables the user to switch
     * between the field's input being visibly disguised or not.
     *
     * @param isEnabled true to enable the functionality
     * @deprecated Use setEndIconMode(int) instead.
     */
    @Deprecated("Use #setEndIconMode(int) instead.")
    open fun setPasswordToggleEnabled(isEnabled:Boolean) {
        fieldState.isPasswordVisibilityToggleEnabled = isEnabled
    }

    /**
     * Set the icon to use for the password visibility toggle button.
     *
     * <p>If you use an icon you should also set a description for its action using setPasswordVisibilityToggleContentDescription(CharSequence).
     * This is used for accessibility.
     *
     * @param resId resource id of the drawable to set, or 0 to clear the icon
     * @deprecated Use setEndIconDrawable(int) instead.
     */
    @Deprecated("Use #setEndIconDrawable(int) instead.")
    open fun setPasswordVisibilityToggleDrawable(@DrawableRes resId:Int) {
        fieldState.passwordVisibilityToggleDrawable = resId
    }

    /**
     * Applies a tint to the password visibility toggle drawable. Does not modify the current tint
     * mode, which is PorterDuff.Mode#SRC_IN by default.
     *
     * <p>Subsequent calls to setPasswordVisibilityToggleDrawable(Drawable) will
     * automatically mutate the drawable and apply the specified tint and tint mode using
     * DrawableCompat.setTintList(Drawable, ColorStateList).
     *
     * @param tintList the tint to apply, may be null to clear tint
     * @deprecated Use setEndIconTintList(ColorStateList) instead.
     */
    open fun setPasswordVisibilityToggleTintList(tintList: ColorStateList?) {
        fieldState.passwordToggleTint = tintList
    }

    /**
     * Set the box's corner radii.
     *
     * @param boxCornerRadiusTopStart the value to use for the box's top start corner radius
     * @param boxCornerRadiusTopEnd the value to use for the box's top end corner radius
     * @param boxCornerRadiusBottomStart the value to use for the box's bottom start corner radius
     * @param boxCornerRadiusBottomEnd the value to use for the box's bottom end corner radius
     * @see #getBoxCornerRadiusTopStart()
     * @see #getBoxCornerRadiusTopEnd()
     * @see #getBoxCornerRadiusBottomStart()
     * @see #getBoxCornerRadiusBottomEnd()
     */
    open fun setBoxCornerRadius(boxCornerRadiusTopStart:Float,
                                boxCornerRadiusTopEnd:Float,
                                boxCornerRadiusBottomStart:Float,
                                boxCornerRadiusBottomEnd:Float) {
        fieldState.boxCornerRadiusTopStart = boxCornerRadiusTopStart
        fieldState.boxCornerRadiusTopEnd = boxCornerRadiusTopEnd
        fieldState.boxCornerRadiusBottomStart = boxCornerRadiusBottomStart
        fieldState.boxCornerRadiusBottomEnd = boxCornerRadiusBottomEnd
    }

    /**
     * Set the box background mode (filled, outline, or none).
     *
     * <p>May be one of BOX_BACKGROUND_NONE, BOX_BACKGROUND_FILLED, or BOX_BACKGROUND_OUTLINE.
     *
     * <p>Note: This method defines TextInputLayout's internal behavior (for example, it allows the
     * hint to be displayed inline with the stroke in a cutout), but doesn't set all attributes that
     * are set in the styles provided for the box background modes. To achieve the look of an outlined
     * or filled text field, supplement this method with other methods that modify the box, such as
     * setBoxStrokeColor(int) and setBoxBackgroundColor(int).
     *
     * @param boxBackgroundMode box's background mode
     */
    open fun setBoxBackgroundMode(boxBackgroundMode:Int) {
        fieldState.boxBackgroundMode = boxBackgroundMode
    }

    /**
     * Set the filled box's background color.
     *
     * <p>Note: The background color is only supported for filled boxes. When used with box variants
     * other than BoxBackgroundMode.BOX_BACKGROUND_FILLED, the box background color may not
     * work as intended.
     *
     * @param boxBackgroundColor the color to use for the filled box's background
     * @see #getBoxBackgroundColor()
     */
    open fun setBoxBackgroundColor(boxBackgroundColor:Int) {
        fieldState.boxBackgroundColor = boxBackgroundColor
    }

    /**
     * Set the outline box's stroke color.
     *
     * <p>Calling this method when not in outline box mode will do nothing.
     *
     * @param boxStrokeColor the color to use for the box's stroke
     * @see #getBoxStrokeColor()
     */
    open fun setBoxStrokeColor(boxStrokeColor:Int) {
        fieldState.boxStrokeColor = boxStrokeColor
    }

    /**
     * Set the outline box's stroke color state list.
     *
     * <p>Calling this method when not in outline box mode will do nothing.
     *
     * @param colorStateList the color state list to use for the box's stroke
     * @see #getBoxStrokeColor()
     */
    open fun setBoxStrokeColorStateList(colorStateList: ColorStateList) {
        fieldState.boxStrokeColorStateList = colorStateList
    }

    /**
     * Sets whether the floating label functionality is enabled or not in this layout.
     *
     * <p>If enabled, any non-empty hint in the child EditText will be moved into the floating hint,
     * and its existing hint will be cleared. If disabled, then any non-empty floating hint in this
     * layout will be moved into the EditText, and this layout's hint will be cleared.
     */
    open fun setHintEnabled(state:Boolean) {
        fieldState.isHintEnabled = state
    }

    /**
     * Set whether any hint state changes, due to being focused or non-empty text, are animated.
     */
    open fun setHintAnimationEnabled(state:Boolean) {
        fieldState.isHintAnimationEnabled = state
    }

    /**
     * Whether the character counter functionality is enabled or not in this layout.
     */
    fun setCounterEnabled(state:Boolean) {
        fieldState.isCounterEnabled = state
    }

    /**
     * Sets the max length to display at the character counter.
     *
     * @param maxLength maxLength to display. Any value less than or equal to 0 will not be shown.
     */
    fun setCounterMaxLength(maxLength:Int) {
        fieldState.counterMaxLength = maxLength
    }

    /**
     * Sets the start icon.
     *
     * <p>If you use an icon you should also set a description for its action using
     * setStartIconContentDescription(CharSequence). This is used for accessibility.
     *
     * @param resId resource id of the drawable to set, or 0 to clear and remove the icon
     */
    fun setStartIconDrawable(resId:Int) {
        fieldState.startIconDrawable = resId
    }

    /**
     * Applies a tint to the start icon drawable. Does not modify the current tint mode, which is
     * {@link PorterDuff.Mode#SRC_IN} by default.
     *
     * <p>Subsequent calls to setStartIconDrawable(Drawable) will automatically mutate the
     * drawable and apply the specified tint and tint mode using
     * DrawableCompat.setTintList(Drawable, ColorStateList).
     *
     * @param startIconTintList the tint to apply, may be null to clear tint
     */
    fun setStartIconDrawableTintList(startIconTintList : ColorStateList?) {
        fieldState.startIconTintList = startIconTintList
    }

    /**
     * Sets the start icon's functionality that is performed when the start icon is clicked. The icon
     * will not be clickable if its click and long click listeners are null.
     *
     * @param startIconOnClickListener the android.view.View.OnClickListener the start icon
     *     view will have, or null to clear it.
     */
    fun setStartIconOnClickListener(startIconOnClickListener : OnClickListener?) {
        fieldState.startIconOnClickListener = startIconOnClickListener
    }

    /**
     * Set the icon to use for the end icon.
     *
     * <p>If you use an icon you should also set a description for its action using
     * setEndIconContentDescription(CharSequence). This is used for accessibility.
     *
     * @param resId resource id of the drawable to set, or 0 to clear the icon
     */
    fun setEndIconDrawable(resId:Int) {
        fieldState.endIconDrawable = resId
    }

    /**
     * Applies a tint to the end icon drawable. Does not modify the current tint mode, which is
     * PorterDuff.Mode#SRC_IN by default.
     *
     * <p>Subsequent calls to setEndIconDrawable(Drawable) will automatically mutate the
     * drawable and apply the specified tint and tint mode using
     * DrawableCompat#setTintList(Drawable, ColorStateList).
     *
     * @param endIconTintList the tint to apply, may be null to clear tint
     */
    fun setEndIconDrawableTintList(endIconTintList : ColorStateList?) {
        fieldState.endIconTintList = endIconTintList
    }

    /**
     * Set up the EndIconMode. When set, a button is placed at the end of the EditText which
     * enables the user to perform the specific icon's functionality.
     *
     * @param endIconMode the EndIconMode to be set, or END_ICON_NONE to clear the current
     * icon if any
     */
    fun setEndIconMode(mode:Int) {
        if(mode <= VGSTextInputLayout.END_ICON_CLEAR_TEXT) {
            fieldState.endIconMode = mode
        }
    }

    /**
     * Returns the current {@link EndIconMode}.
     *
     * @return the end icon mode enum
     */
    fun getEndIconMode():Int {
        return fieldState.endIconMode
    }

    /**
     * Sets the end icon's functionality that is performed when the icon is clicked. The icon will not
     * be clickable if its click and long click listeners are null.
     *
     * @param endIconOnClickListener the android.view.View.OnClickListener the end icon view
     * will have
     */
    fun setEndIconOnClickListener(endIconOnClickListener : OnClickListener?) {
        fieldState.endIconOnClickListener = endIconOnClickListener
    }


    @VisibleForTesting
    internal fun getFieldState():InputLayoutStateImpl {
        return fieldState
    }

    /**
     * Sets the typeface and style in which the text should be displayed.
     *
     * @param typeface This value may be null.
     */
    open fun setTypeface(typeface: Typeface) {
        fieldState.typeface = typeface
    }

    /**
     * Returns the typeface used for the hint and any label views (such as counter and error views).
     */
    fun getTypeface():Typeface? {
        return fieldState.typeface
    }

    /**
     * Sets the collapsed hint text color from the specified ColorStateList resource.
     */
    fun setHintTextColor(hintTextColor:ColorStateList) {
        fieldState.hintTextColor = hintTextColor
    }

    /**
     * Gets the collapsed hint text color.
     */
    fun getHintTextColor() : ColorStateList? {
        return fieldState.hintTextColor
    }

    /**
     * Sets the text color and size for the error message from the specified TextAppearance resource.
     */
    fun setErrorTextAppearance(@StyleRes resId: Int) {
        fieldState.errorTextAppearance = resId
    }

    /**
     * Sets the collapsed hint text color, size, style from the specified TextAppearance resource.
     */
    fun setHintTextAppearance(@StyleRes resId:Int) {
        fieldState.hintTextAppearance = resId
    }

    /**
     * Sets the text color and size for the helper text from the specified TextAppearance resource.
     */
    fun setHelperTextTextAppearance(@StyleRes resId:Int) {
        fieldState.helperTextTextAppearance = resId
    }

    /**
     * Sets the text color and size for the character counter using the specified TextAppearance
     * resource.
     */
    fun setCounterTextAppearance(counterTextAppearance:Int) {
        fieldState.counterTextAppearance = counterTextAppearance
    }

    /**
     * Sets the text color and size for the overflowed character counter using the specified
     * TextAppearance resource.
     */
    fun setCounterOverflowTextAppearance(counterOverflowTextAppearance:Int) {
        fieldState.counterOverflowTextAppearance = counterOverflowTextAppearance
    }

    /**
     * Sets a helper message that will be displayed below the {@link EditText}. If the helperText
     * is null, the helper text functionality will be disabled and the helper message will be
     * hidden.
     *
     * If the helper text functionality has not been enabled via setHelperTextEnabled(boolean),
     * then it will be automatically enabled if helperText is not empty.
     *
     * @param helperText Helper text to display
     * @see #getHelperText()
     */
    fun setHelperText(helperText:String?) {
        fieldState.helperText = helperText
    }

    /**
     * Returns the helper message that was set to be displayed with setHelperText(CharSequence),
     * or null if no helper text was set or if helper text functionality is not enabled.
     *
     * @see #setHelperText(CharSequence)
     */
    fun getHelperText():String? {
        return fieldState.helperText
    }

}
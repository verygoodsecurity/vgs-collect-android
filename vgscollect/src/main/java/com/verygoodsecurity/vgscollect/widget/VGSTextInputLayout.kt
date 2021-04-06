package com.verygoodsecurity.vgscollect.widget

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import com.google.android.material.textfield.TextInputLayout
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.view.material.TextInputFieldLayout


/**
 * Material component which wraps an VGS field to show a floating label when
 * the hint is hidden due to user inputting text.
 */
class VGSTextInputLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextInputFieldLayout(context, attrs, defStyleAttr) {

    companion object {
        internal val TAG = VGSTextInputLayout::class.simpleName.toString()

        /**
         * The TextInputLayout will show a custom icon specified by the user.
         */
        const val END_ICON_CUSTOM = -1

        /**
         * Default for the TextInputLayout. It will not display an end icon.
         *
         * @see setEndIconMode
         * @see getEndIconMode
         */
        const val END_ICON_NONE = 0

        /**
         * The TextInputLayout will show a password toggle button if its EditText displays a password.
         * When this end icon is clicked, the password is shown as plain-text if it was disguised, or
         * vice-versa.
         *
         * @see setEndIconMode
         * @see getEndIconMode
         */
        const val END_ICON_PASSWORD_TOGGLE = 1

        /**
         * The TextInputLayout will show a clear text button while there is input in the EditText.
         * Clicking it will clear out the text and hide the icon.
         *
         * @see setEndIconMode
         * @see getEndIconMode
         */
        const val END_ICON_CLEAR_TEXT = 2
    }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.VGSTextInputLayout, 0, 0)?.apply {
            try {
                val passwordToggleEnabled = getBoolean(R.styleable.VGSTextInputLayout_passwordToggleEnabled, false)
                val drawRef = getResourceId(R.styleable.VGSTextInputLayout_passwordToggleDrawable, 0)
                val textColor = getColorStateList(R.styleable.VGSTextInputLayout_passwordToggleTint)
                val hintTextColor = getColorStateList(R.styleable.VGSTextInputLayout_hintTextColor)
                val hintTextAppearance = getResourceId(R.styleable.VGSTextInputLayout_hintTextAppearance, 0)
                val counterOverflowTextAppearance = getResourceId(R.styleable.VGSTextInputLayout_counterOverflowTextAppearance, 0)
                val counterTextAppearance = getResourceId(R.styleable.VGSTextInputLayout_counterTextAppearance, 0)
                val helperTextTextAppearance = getResourceId(R.styleable.VGSTextInputLayout_helperTextTextAppearance, 0)
                val isErrorEnabled = getBoolean(R.styleable.VGSTextInputLayout_errorEnabled, false)
                val errorTextAppearance = getResourceId(R.styleable.VGSTextInputLayout_errorTextAppearance, 0)
                val helperText = getString(R.styleable.VGSTextInputLayout_helperText)

                val hint = getString(R.styleable.VGSTextInputLayout_hint)

                val boxBackgroundColor = getColor(R.styleable.VGSTextInputLayout_boxBackgroundColor, 0)
                val boxStrokeColor = getColor(R.styleable.VGSTextInputLayout_boxStrokeColor, getThemeAccentColor(context))
                val boxStrokeColorStateList = getColorStateList(R.styleable.VGSTextInputLayout_boxStrokeColor)
                val boxBackgroundMode = getInteger(R.styleable.VGSTextInputLayout_boxBackgroundModes, 0)
                val boxCornerRadiusBottomEnd = getDimension(R.styleable.VGSTextInputLayout_boxCornerRadiusBottomEnd, 0f)
                val boxCornerRadiusTopStart = getDimension(R.styleable.VGSTextInputLayout_boxCornerRadiusTopStart, 0f)
                val boxCornerRadiusBottomStart = getDimension(R.styleable.VGSTextInputLayout_boxCornerRadiusBottomStart, 0f)
                val boxCornerRadiusTopEnd = getDimension(R.styleable.VGSTextInputLayout_boxCornerRadiusTopEnd, 0f)
                val hintEnabled = getBoolean(R.styleable.VGSTextInputLayout_hintEnabled, true)
                val hintAnimationEnabled = getBoolean(R.styleable.VGSTextInputLayout_hintAnimationEnabled, true)

                val defRadius = resources.getDimension(R.dimen.default_horizontal_field)
                val boxCornerRadius = getDimension(R.styleable.VGSTextInputLayout_boxCornerRadius, defRadius)

                val endIconModes = getInteger(R.styleable.VGSTextInputLayout_endIconModes, END_ICON_NONE)
                val endIconDrawables = getResourceId(R.styleable.VGSTextInputLayout_endIconDrawable, 0)
                val endIconTints = getColorStateList(R.styleable.VGSTextInputLayout_endIconTint)
                val startIconDrawables = getResourceId(R.styleable.VGSTextInputLayout_startIconDrawable, 0)
                val startIconTints = getColorStateList(R.styleable.VGSTextInputLayout_startIconTint)

                val counterEnabled = getBoolean(R.styleable.VGSTextInputLayout_counterEnabled, false)
                val counterMaxLength = getInteger(R.styleable.VGSTextInputLayout_counterMaxLength, -1)

                setStartIconDrawable(startIconDrawables)
                setStartIconDrawableTintList(startIconTints)

                setEndIconMode(endIconModes)
                setEndIconDrawable(endIconDrawables)
                setEndIconDrawableTintList(endIconTints)

                setHint(hint)
                setPasswordToggleEnabled(passwordToggleEnabled)

                when(boxBackgroundMode) {
                    0 -> setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE)
                    1 -> setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_FILLED)
                    2 -> setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_NONE)
                }

                setBoxBackgroundColor(boxBackgroundColor)
                setBoxStrokeColor(boxStrokeColor)
                boxStrokeColorStateList?.let { setBoxStrokeColorStateList(it) }

                setPasswordVisibilityToggleDrawable(drawRef)

                setPasswordVisibilityToggleTintList(textColor)

                val boxTS = if(boxCornerRadiusTopStart == 0f) boxCornerRadius else boxCornerRadiusTopStart
                val boxTE = if(boxCornerRadiusTopEnd == 0f) boxCornerRadius else boxCornerRadiusTopEnd
                val boxBS = if(boxCornerRadiusBottomStart == 0f) boxCornerRadius else boxCornerRadiusBottomStart
                val boxBE = if(boxCornerRadiusBottomEnd == 0f) boxCornerRadius else boxCornerRadiusBottomEnd
                setBoxCornerRadius(boxTS, boxTE, boxBS, boxBE)

                setHintEnabled(hintEnabled)
                setHintAnimationEnabled(hintAnimationEnabled)

                setCounterEnabled(counterEnabled)
                setCounterMaxLength(counterMaxLength)

                setHelperText(helperText)

                if(hintTextColor != null) {
                    setHintTextColor(hintTextColor)
                }

                if(hintTextAppearance != 0) {
                    setHintTextAppearance(hintTextAppearance)
                }
                if(helperTextTextAppearance != 0) {
                    setHelperTextTextAppearance(helperTextTextAppearance)
                }
                if(counterTextAppearance != 0) {
                    setCounterTextAppearance(counterTextAppearance)
                }
                if(counterOverflowTextAppearance != 0) {
                    setCounterOverflowTextAppearance(counterOverflowTextAppearance)
                }
                if(errorTextAppearance != 0) {
                    setErrorTextAppearance(errorTextAppearance)
                }
                setErrorEnabled(isErrorEnabled)


            } finally {
                recycle()
            }
        }
    }

    private fun getThemeAccentColor(context: Context): Int {
        val colorAccentAttr: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            android.R.attr.colorAccent
        } else {
            context.resources.getIdentifier("colorAccent", "attr", context.packageName)
        }
        val outValue = TypedValue()
        context.theme.resolveAttribute(colorAccentAttr, outValue, true)
        return outValue.data
    }
}
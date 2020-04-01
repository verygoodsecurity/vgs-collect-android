package com.verygoodsecurity.vgscollect.widget

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputLayout
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.view.material.TextInputFieldLayout


/**
 * Material component which wraps an VGS field to show a floating label when
 * the hint is hidden due to user inputting text.
 *
 * @since 1.0.0
 */
class VGSTextInputLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextInputFieldLayout(context, attrs, defStyleAttr) {

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.VGSTextInputLayout, 0, 0)?.apply {
            try {
                val passwordToggleEnabled = getBoolean(R.styleable.VGSTextInputLayout_passwordToggleEnabled, false)
                val drawRef = getResourceId(R.styleable.VGSTextInputLayout_passwordToggleDrawable, 0)
                val textColor = getColorStateList(R.styleable.VGSTextInputLayout_passwordToggleTint)
                val hint = getString(R.styleable.VGSTextInputLayout_hint)

                val boxBackgroundColor = getColor(R.styleable.VGSTextInputLayout_boxBackgroundColor, 0)
                val boxStrokeColor = getColor(R.styleable.VGSTextInputLayout_boxStrokeColor, 0)
                val boxBackgroundMode = getInteger(R.styleable.VGSTextInputLayout_boxBackgroundModes, 0)
                val boxCornerRadiusBottomEnd = getDimension(R.styleable.VGSTextInputLayout_boxCornerRadiusBottomEnd, 0f)
                val boxCornerRadiusTopStart = getDimension(R.styleable.VGSTextInputLayout_boxCornerRadiusTopStart, 0f)
                val boxCornerRadiusBottomStart = getDimension(R.styleable.VGSTextInputLayout_boxCornerRadiusBottomStart, 0f)
                val boxCornerRadiusTopEnd = getDimension(R.styleable.VGSTextInputLayout_boxCornerRadiusTopEnd, 0f)
                val hintEnabled = getBoolean(R.styleable.VGSTextInputLayout_hintEnabled, true)
                val hintAnimationEnabled = getBoolean(R.styleable.VGSTextInputLayout_hintAnimationEnabled, true)

                val defRadius = resources.getDimension(R.dimen.default_horizontal_field)
                val boxCornerRadius = getDimension(R.styleable.VGSTextInputLayout_boxCornerRadius, defRadius)

                setHint(hint)
                setPasswordToggleEnabled(passwordToggleEnabled)

                when(boxBackgroundMode) {
                    0 -> setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE)
                    1 -> setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_FILLED)
                    2 -> setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_NONE)
                }

                setBoxBackgroundColor(boxBackgroundColor)
                setBoxStrokeColor(boxStrokeColor)

                setPasswordVisibilityToggleDrawable(drawRef)

                setPasswordVisibilityToggleTintList(textColor)

                val boxTS = if(boxCornerRadiusTopStart == 0f) boxCornerRadius else boxCornerRadiusTopStart
                val boxTE = if(boxCornerRadiusTopEnd == 0f) boxCornerRadius else boxCornerRadiusTopEnd
                val boxBS = if(boxCornerRadiusBottomStart == 0f) boxCornerRadius else boxCornerRadiusBottomStart
                val boxBE = if(boxCornerRadiusBottomEnd == 0f) boxCornerRadius else boxCornerRadiusBottomEnd
                setBoxCornerRadius(boxTS, boxTE, boxBS, boxBE)

                setHintEnabled(hintEnabled)
                setHintAnimationEnabled(hintAnimationEnabled)
            } finally {
                recycle()
            }
        }
    }
}
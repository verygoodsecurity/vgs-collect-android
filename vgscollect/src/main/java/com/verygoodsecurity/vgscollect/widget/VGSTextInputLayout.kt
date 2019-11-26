package com.verygoodsecurity.vgscollect.widget

import android.content.Context
import android.util.AttributeSet
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.view.TextInputFieldLayout

class VGSTextInputLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextInputFieldLayout(context, attrs, defStyleAttr) {

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.VGSTextInputLayout, 0, 0)?.apply {
            try {
                val passwordToggleEnabled = getBoolean(R.styleable.VGSTextInputLayout_passwordToggleEnabled, false)
                val drawRef = getResourceId(R.styleable.VGSTextInputLayout_passwordToggleDrawable, -1)
                val textColor = getColorStateList(R.styleable.VGSTextInputLayout_passwordToggleTint)
                val hint = getString(R.styleable.VGSTextInputLayout_hint)

                setHint(hint)
                setPasswordToggleEnabled(passwordToggleEnabled)
                if(drawRef > 0) {
                    setPasswordVisibilityToggleDrawable(drawRef)
                }

                setPasswordVisibilityToggleTintList(textColor)
            } finally {
                recycle()
            }
        }
    }
}
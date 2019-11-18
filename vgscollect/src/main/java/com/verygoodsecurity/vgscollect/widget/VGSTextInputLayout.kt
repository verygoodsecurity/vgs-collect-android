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
            R.styleable.VGSTextInputLayout,
            0, 0).apply {
            try {
                val hint = getString(R.styleable.VGSTextInputLayout_hint)

                setHint(hint)
            } finally {
                recycle()
            }
        }
    }
}
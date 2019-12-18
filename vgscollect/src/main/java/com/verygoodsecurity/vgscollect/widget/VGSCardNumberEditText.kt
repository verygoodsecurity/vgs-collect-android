package com.verygoodsecurity.vgscollect.widget

import android.content.Context
import android.util.AttributeSet
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.view.card.CustomCardBrand

class VGSCardNumberEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : VGSEditText(context, attrs, defStyleAttr) {


    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.VGSCardNumberEditText,
            0, 0
        ).apply {

            try {
                val previewGravity = getInt(R.styleable.VGSCardNumberEditText_cardBrandIconGravity, 0)
                val divider = getString(R.styleable.VGSCardNumberEditText_numberDivider)

                setNumberDivider(divider)
                applyCardIconGravity(previewGravity)
            } finally {
                recycle()
            }
        }
    }

    fun setCardBrandIconGravity(gravity:Int) {
        applyCardIconGravity(gravity)
    }

    fun addCardBrand(c: CustomCardBrand) {
        applyCardBrand(c)
    }
}
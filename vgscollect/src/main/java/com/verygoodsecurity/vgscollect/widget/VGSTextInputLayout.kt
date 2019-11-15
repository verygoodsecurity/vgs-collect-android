package com.verygoodsecurity.vgscollect.widget

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
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

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
    }

    override fun getPaddingBottom(): Int {
        return super.getPaddingBottom()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun getPaddingEnd(): Int {
        return super.getPaddingEnd()
    }

    override fun getPaddingLeft(): Int {
        return super.getPaddingLeft()
    }

    override fun getPaddingRight(): Int {
        return super.getPaddingRight()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun getPaddingStart(): Int {
        return super.getPaddingStart()
    }

    override fun getPaddingTop(): Int {
        return super.getPaddingTop()
    }

    override fun addView(child: View?) {
        super.addView(child)
    }

    override fun addView(child: View?, index: Int) {
        super.addView(child, index)
    }

    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
        super.addView(child, params)
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        super.addView(child, index, params)
    }

    override fun addView(child: View?, width: Int, height: Int) {
        super.addView(child, width, height)
    }

    override fun setError(errorText:CharSequence?) {
        super.setError(errorText)
    }

    override fun setHint(text:String?) {
        super.setHint(text)
    }
}
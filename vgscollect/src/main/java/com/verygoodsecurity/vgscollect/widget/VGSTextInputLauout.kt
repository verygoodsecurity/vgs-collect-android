package com.verygoodsecurity.vgscollect.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.textfield.TextInputLayout
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.view.TextInputLayoutWrapper

class VGSTextInputLauout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val textInputLauout: TextInputLayout
    var editText:VGSEditText? = null
        private set

    init {
        val hint:String?
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.VGSTextInputLauout,
            0, 0).apply {

            try {
                hint = getString(R.styleable.VGSTextInputLauout_hint)
            } finally {
                recycle()
            }
        }

        textInputLauout = TextInputLayoutWrapper(context)
        textInputLauout.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        textInputLauout.hint = hint
        addView(textInputLauout)
    }

    override fun addView(child: View?) {
        val v = handleNewChild(child)
        super.addView(v)
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

    private fun handleNewChild(child: View?): View? {
        return child?.run {
            when(this) {
                is TextInputLayoutWrapper -> this
                is VGSEditText -> {
                    editText = child as VGSEditText
                    textInputLauout.addView(child)
                    null
                }
                else -> null
            }
        }
    }





    fun setHint(text:String) {
        textInputLauout.hint = text
    }

//    override fun onAttachedToWindow() {
//        super.onAttachedToWindow()
//        addView(textInputLauout)
//    }
}
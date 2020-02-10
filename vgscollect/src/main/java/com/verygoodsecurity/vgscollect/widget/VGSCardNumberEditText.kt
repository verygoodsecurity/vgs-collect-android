package com.verygoodsecurity.vgscollect.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.inputmethod.EditorInfo
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.card.CustomCardBrand
import com.verygoodsecurity.vgscollect.view.card.FieldType

class VGSCardNumberEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : InputFieldView(context, attrs, defStyleAttr) {


    init {
        setupViewType(FieldType.CARD_NUMBER)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.VGSCardNumberEditText,
            0, 0
        ).apply {

            try {
                val previewGravity = getInt(R.styleable.VGSCardNumberEditText_cardBrandIconGravity, 0)
                val divider:String = getString(R.styleable.VGSCardNumberEditText_numberDivider)?:" "

                val cursorColor = getColor(R.styleable.VGSCardNumberEditText_cursorColor, 0)
                val inputType = getInt(R.styleable.VGSCardNumberEditText_inputType, EditorInfo.TYPE_NULL)
                val fieldName = getString(R.styleable.VGSCardNumberEditText_fieldName)
                val hint = getString(R.styleable.VGSCardNumberEditText_hint)
                val textSize = getDimension(R.styleable.VGSCardNumberEditText_textSize, -1f)
                val textColor = getColor(R.styleable.VGSCardNumberEditText_textColor, Color.BLACK)
                val text = getString(R.styleable.VGSCardNumberEditText_text)
                val textStyle = getInt(R.styleable.VGSCardNumberEditText_textStyle, -1)
                val cursorVisible = getBoolean(R.styleable.VGSCardNumberEditText_cursorVisible, true)
                val enabled = getBoolean(R.styleable.VGSCardNumberEditText_enabled, true)
                val isRequired = getBoolean(R.styleable.VGSCardNumberEditText_isRequired, true)
                val singleLine = getBoolean(R.styleable.VGSCardNumberEditText_singleLine, true)
                val scrollHorizontally = getBoolean(R.styleable.VGSCardNumberEditText_scrollHorizontally, true)
                val gravity = getInt(R.styleable.VGSCardNumberEditText_gravity, 0)
                val ellipsize = getInt(R.styleable.VGSCardNumberEditText_ellipsize, 0)

                val minLines = getInt(R.styleable.VGSCardNumberEditText_minLines, 0)
                val maxLines = getInt(R.styleable.VGSCardNumberEditText_maxLines, 0)

                setFieldName(fieldName)
                setHint(hint)
                setTextColor(textColor)
                setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
                setCursorVisible(cursorVisible)
                setGravity(gravity)
                canScrollHorizontally(scrollHorizontally)
                setEllipsize(ellipsize)
                setMaxLines(maxLines)
                setMinLines(minLines)
                setSingleLine(singleLine)
                setIsRequired(isRequired)
                getTypeface()?.let {
                    setTypeface(it, textStyle)
                }

                setText(text)
                setEnabled(enabled)

                setInputType(inputType)
                if(cursorColor != 0) {
                    setCursorColor(cursorColor)
                }

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

    fun setDivider(char:Char) {
        setNumberDivider(char.toString())
    }
}
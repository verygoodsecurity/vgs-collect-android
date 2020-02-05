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

/**
 * A user interface element that displays text to the user in card number format.
 *
 * @version 1.0.2
 */
class VGSCardNumberEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : InputFieldView(context, attrs, defStyleAttr) {


    init {
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
                setFieldType(FieldType.CARD_NUMBER)
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

    /**
     * Specifies how to align the icon by the view’s x-axis. To specify gravity programmatically
     * you could use android Gravity class.
     *
     * @param gravity Specifies how to align the icon by the view’s x-axis.
     */
    fun setCardBrandIconGravity(gravity:Int) {
        applyCardIconGravity(gravity)
    }

    /**
     * It may be useful to add new brands in addition to already defined brands or override existing ones.
     *
     * @param c new card definition
     */
    fun addCardBrand(c: CustomCardBrand) {
        applyCardBrand(c)
    }

    /**
     * Sets the symbol that will divide groups of digits in the card number.
     * 0000 0000 0000 0000
     *
     * @param char The divider symbol.
     */
    fun setDivider(char:Char) {
        setNumberDivider(char.toString())
    }
}
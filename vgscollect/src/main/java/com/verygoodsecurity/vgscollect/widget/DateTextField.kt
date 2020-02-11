package com.verygoodsecurity.vgscollect.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.inputmethod.EditorInfo
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.card.FieldType

class DateTextField @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : InputFieldView(context, attrs, defStyleAttr) {


    init {
        setupViewType(FieldType.CARD_EXPIRATION_DATE)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.DateTextField,
            0, 0
        ).apply {

            try {
                val datePattern = getString(R.styleable.DateTextField_datePattern)
                val datePickerMode = getInt(R.styleable.DateTextField_datePickerMode, 1)

                val inputType = getInt(R.styleable.DateTextField_inputType, EditorInfo.TYPE_NULL)
                val fieldName = getString(R.styleable.DateTextField_fieldName)
                val hint = getString(R.styleable.DateTextField_hint)
                val textSize = getDimension(R.styleable.DateTextField_textSize, -1f)
                val textColor = getColor(R.styleable.DateTextField_textColor, Color.BLACK)
                val text = getString(R.styleable.DateTextField_text)
                val textStyle = getInt(R.styleable.DateTextField_textStyle, -1)
                val cursorVisible = getBoolean(R.styleable.DateTextField_cursorVisible, true)
                val enabled = getBoolean(R.styleable.DateTextField_enabled, true)
                val isRequired = getBoolean(R.styleable.DateTextField_isRequired, true)
                val singleLine = getBoolean(R.styleable.DateTextField_singleLine, true)
                val scrollHorizontally = getBoolean(R.styleable.DateTextField_scrollHorizontally, true)
                val gravity = getInt(R.styleable.DateTextField_gravity, 0)
                val ellipsize = getInt(R.styleable.DateTextField_ellipsize, 0)

                setFieldName(fieldName)
                setHint(hint)
                setTextColor(textColor)
                setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
                setCursorVisible(cursorVisible)
                setGravity(gravity)
                canScrollHorizontally(scrollHorizontally)
                setEllipsize(ellipsize)
                setSingleLine(singleLine)
                setIsRequired(isRequired)
                getTypeface()?.let {
                    setTypeface(it, textStyle)
                }

                setText(text)
                setEnabled(enabled)

                setInputType(inputType)

                setDatePattern(datePattern)
                setDatePickerMode(datePickerMode)
            } finally {
                recycle()
            }
        }
    }

}
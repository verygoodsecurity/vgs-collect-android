package com.verygoodsecurity.vgscollect.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.view.InputFieldView

class VGSEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : InputFieldView(context, attrs, defStyleAttr) {

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.VGSEditText,
            0, 0
        ).apply {

            try {
                val type = getInteger(R.styleable.VGSEditText_fieldType, -1)
                val fieldName = getString(R.styleable.VGSEditText_fieldName)
                val hint = getString(R.styleable.VGSEditText_hint)
                val textSize = getDimension(R.styleable.VGSEditText_textSize, -1f)
                val textColor = getColor(R.styleable.VGSEditText_textColor, Color.BLACK)
                val text = getString(R.styleable.VGSEditText_text)
                val textStyle = getInt(R.styleable.VGSEditText_textStyle, -1)
                val cursorVisible = getBoolean(R.styleable.VGSEditText_cursorVisible, true)
                val isRequired = getBoolean(R.styleable.VGSEditText_isRequired, true)
                val singleLine = getBoolean(R.styleable.VGSEditText_singleLine, true)
                val scrollHorizontally = getBoolean(R.styleable.VGSEditText_scrollHorizontally, true)
                val gravity = getInt(R.styleable.VGSEditText_gravity, 0)
                val ellipsize = getInt(R.styleable.VGSEditText_ellipsize, 0)

                val minLines = getInt(R.styleable.VGSEditText_minLines, 0)
                val maxLines = getInt(R.styleable.VGSEditText_maxLines, 0)

                setFieldName(fieldName)
                setFieldType(type)
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
                setTypeface(getTypeface(), textStyle)
                setText(text)
            } finally {
                recycle()
            }
        }
    }
}
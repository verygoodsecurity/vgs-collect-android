package com.verygoodsecurity.vgscollect.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.card.FieldType

class CardVerificationCodeEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : InputFieldView(context, attrs, defStyleAttr) {

    init {
        setupViewType(FieldType.CVC)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CardVerificationCodeEditText,
            0, 0
        ).apply {

            try {
                val inputType =
                    getInt(R.styleable.CardVerificationCodeEditText_inputType, EditorInfo.TYPE_NULL)
                val fieldName = getString(R.styleable.CardVerificationCodeEditText_fieldName)
                val hint = getString(R.styleable.CardVerificationCodeEditText_hint)
                val textSize = getDimension(R.styleable.CardVerificationCodeEditText_textSize, -1f)
                val textColor =
                    getColor(R.styleable.CardVerificationCodeEditText_textColor, Color.BLACK)
                val text = getString(R.styleable.CardVerificationCodeEditText_text)
                val textStyle = getInt(R.styleable.CardVerificationCodeEditText_textStyle, -1)
                val cursorVisible =
                    getBoolean(R.styleable.CardVerificationCodeEditText_cursorVisible, true)
                val enabled = getBoolean(R.styleable.CardVerificationCodeEditText_enabled, true)
                val isRequired =
                    getBoolean(R.styleable.CardVerificationCodeEditText_isRequired, true)
                val singleLine =
                    getBoolean(R.styleable.CardVerificationCodeEditText_singleLine, true)
                val scrollHorizontally =
                    getBoolean(R.styleable.CardVerificationCodeEditText_scrollHorizontally, true)
                val gravity = getInt(R.styleable.CardVerificationCodeEditText_gravity, Gravity.START or Gravity.CENTER_VERTICAL)
                val ellipsize = getInt(R.styleable.CardVerificationCodeEditText_ellipsize, 0)

                val minLines = getInt(R.styleable.CardVerificationCodeEditText_minLines, 0)
                val maxLines = getInt(R.styleable.CardVerificationCodeEditText_maxLines, 0)

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
            } finally {
                recycle()
            }
        }
    }

    /**
     * It return current state of the field.
     *
     * @return current state.
     */
    fun getState(): FieldState.CVCState? {
        return getCVCState()
    }
}
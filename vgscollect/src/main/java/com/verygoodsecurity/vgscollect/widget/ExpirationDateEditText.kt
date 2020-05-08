package com.verygoodsecurity.vgscollect.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.inputmethod.EditorInfo
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.date.DatePickerMode

/**
 * Provides a user interface element for date input. The range of dates supported by this field is not configurable.
 *
 * @since 1.0.7
 */
class ExpirationDateEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : InputFieldView(context, attrs, defStyleAttr) {


    init {
        setupViewType(FieldType.CARD_EXPIRATION_DATE)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ExpirationDateEditText,
            0, 0
        ).apply {

            try {
                val datePattern = getString(R.styleable.ExpirationDateEditText_datePattern)
                val outputPattern = getString(R.styleable.ExpirationDateEditText_outputPattern)
                val datePickerMode = getInt(R.styleable.ExpirationDateEditText_datePickerModes, 1)

                val inputType = getInt(R.styleable.ExpirationDateEditText_inputType, EditorInfo.TYPE_NULL)
                val fieldName = getString(R.styleable.ExpirationDateEditText_fieldName)
                val hint = getString(R.styleable.ExpirationDateEditText_hint)
                val textSize = getDimension(R.styleable.ExpirationDateEditText_textSize, -1f)
                val textColor = getColor(R.styleable.ExpirationDateEditText_textColor, Color.BLACK)
                val text = getString(R.styleable.ExpirationDateEditText_text)
                val textStyle = getInt(R.styleable.ExpirationDateEditText_textStyle, -1)
                val cursorVisible = getBoolean(R.styleable.ExpirationDateEditText_cursorVisible, true)
                val enabled = getBoolean(R.styleable.ExpirationDateEditText_enabled, true)
                val isRequired = getBoolean(R.styleable.ExpirationDateEditText_isRequired, true)
                val singleLine = getBoolean(R.styleable.ExpirationDateEditText_singleLine, true)
                val scrollHorizontally = getBoolean(R.styleable.ExpirationDateEditText_scrollHorizontally, true)
                val gravity = getInt(R.styleable.ExpirationDateEditText_gravity, 0)
                val ellipsize = getInt(R.styleable.ExpirationDateEditText_ellipsize, 0)

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

                setOutputPattern(outputPattern)
            } finally {
                recycle()
            }
        }
        setMinDate(System.currentTimeMillis())
    }

    /**
     * Representation of date and times which will be sent to the Vault Proxy Server. The method uses the ISO 8601 standard.
     *
     * @param regex Specifies date representation format
     */
    fun setOutputRegex(regex:String) {
        setOutputPattern(regex)
    }

    /**
     * Representation of dates and times is an international standard covering the exchange of date- and time-related data.
     * The method uses the ISO 8601 standard.
     *
     * @param regex Specifies date representation format
     *
     */
    fun setDateRegex(regex:String) {
        setDatePattern(regex)
    }

    /**
     * Return regex date representation format.
     *
     * @return regex
     */
    fun getDateRegex():String? = getDatePattern()

    /**
     * Sets type of exact appearance and interaction model of this widget.
     *
     * @param mode
     */
    fun setDatePickerMode(mode: DatePickerMode) {
        setDatePickerMode(mode.ordinal)
    }

    /**
     * Return type of exact appearance and interaction model of this widget.
     *
     * @return date picker mode
     */
    fun getDatePickerMode():DatePickerMode? = getDateMode()

}
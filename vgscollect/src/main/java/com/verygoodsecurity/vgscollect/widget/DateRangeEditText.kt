package com.verygoodsecurity.vgscollect.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultAliasFormat
import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultStorageType
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.formatter.rules.FormatMode

class DateRangeEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : InputFieldView(context, attrs, defStyleAttr) {

    init {
        setupViewType(FieldType.DATE_RANGE)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.DateRangeEditText,
            0,
            0
        ).apply {
            try {
                val formatterMode = getInt(
                    R.styleable.DateRangeEditText_formatterMode,
                    FormatMode.STRICT.ordinal
                )
                setFormatterMode(formatterMode)

                val datePattern = getString(R.styleable.DateRangeEditText_datePattern)
                setDatePattern(datePattern)

                val outputPattern = getString(R.styleable.DateRangeEditText_outputPattern)
                setOutputPattern(outputPattern)

                val datePickerMode = getInt(R.styleable.DateRangeEditText_datePickerModes, 1)
                setDatePickerMode(datePickerMode)

                val inputType =
                    getInt(R.styleable.DateRangeEditText_inputType, EditorInfo.TYPE_NULL)
                setInputType(inputType)

                val fieldName = getString(R.styleable.DateRangeEditText_fieldName)
                setFieldName(fieldName)

                val hint = getString(R.styleable.DateRangeEditText_hint)
                setHint(hint)

                val textSize = getDimension(R.styleable.DateRangeEditText_textSize, -1f)
                setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)

                val textColor = getColor(R.styleable.DateRangeEditText_textColor, Color.BLACK)
                setTextColor(textColor)

                val text = getString(R.styleable.DateRangeEditText_text)
                setText(text)

                val textStyle = getInt(R.styleable.DateRangeEditText_textStyle, -1)
                getTypeface()?.let {
                    setTypeface(it, textStyle)
                }

                val cursorVisible = getBoolean(R.styleable.DateRangeEditText_cursorVisible, true)
                setCursorVisible(cursorVisible)

                val enabled = getBoolean(R.styleable.DateRangeEditText_enabled, true)
                setEnabled(enabled)

                val isRequired = getBoolean(R.styleable.DateRangeEditText_isRequired, true)
                setIsRequired(isRequired)

                val singleLine = getBoolean(R.styleable.DateRangeEditText_singleLine, true)
                setSingleLine(singleLine)

                val scrollHorizontally =
                    getBoolean(R.styleable.DateRangeEditText_scrollHorizontally, true)
                canScrollHorizontally(scrollHorizontally)

                val gravity = getInt(
                    R.styleable.DateRangeEditText_gravity,
                    Gravity.START or Gravity.CENTER_VERTICAL
                )
                setGravity(gravity)

                val ellipsize = getInt(R.styleable.DateRangeEditText_ellipsize, 0)
                setEllipsize(ellipsize)

                val aliasFormat = getInt(
                    R.styleable.DateRangeEditText_aliasFormat,
                    VGSVaultAliasFormat.UUID.ordinal
                )
                applyAliasFormat(VGSVaultAliasFormat.values()[aliasFormat])

                val storageType = getInt(
                    R.styleable.DateRangeEditText_storageType,
                    VGSVaultStorageType.PERSISTENT.ordinal
                )
                applyStorageType(VGSVaultStorageType.values()[storageType])

                val enableTokenization =
                    getBoolean(R.styleable.DateRangeEditText_enableTokenization, true)
                enableTokenization(enableTokenization)
            } finally {
                recycle()
            }
        }
    }

    /**
     * Return regex date representation format.
     *
     * @return regex
     */
//    fun getDateRegex(): String? = getDa tePattern()
}
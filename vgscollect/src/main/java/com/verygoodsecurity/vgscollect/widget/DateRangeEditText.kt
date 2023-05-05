package com.verygoodsecurity.vgscollect.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultAliasFormat
import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultStorageType
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.formatter.rules.FormatMode
import com.verygoodsecurity.vgscollect.view.core.serializers.FieldDataSerializer
import com.verygoodsecurity.vgscollect.view.date.DatePickerMode

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

                val inputType = getInt(R.styleable.DateRangeEditText_inputType, EditorInfo.TYPE_NULL)
                setInputType(inputType)

                val fieldName = getString(R.styleable.DateRangeEditText_fieldName)
                setFieldName(fieldName)

                val startDate = getString(R.styleable.DateRangeEditText_startDate)
                if (startDate != null) {
                    setStartDate(startDate)
                }

                val endDate = getString(R.styleable.DateRangeEditText_endDate)
                if (endDate != null) {
                    setEndDate(endDate)
                }

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
     * Representation of date and times which will be sent to the Vault Proxy Server. The method uses the ISO 8601 standard.
     *
     * @param regex Specifies date representation format
     */
    fun setOutputRegex(regex: String) {
        setOutputPattern(regex)
    }

    /**
     * Representation of dates and times is an international standard covering the exchange of date and time related data.
     * The method uses the ISO 8601 standard.
     *
     * @param regex Specifies date representation format
     */
    fun setDateRegex(regex: String) {
        setDatePattern(regex)
    }

    fun setStartDate(date: String) {
        minDate(date)
    }

    fun setEndDate(date: String) {
        maxDate(date)
    }

    /**
     * Return regex date representation format.
     *
     * @return regex
     */
    fun getDateRegex(): String? = getDatePattern()

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
    fun getDatePickerMode(): DatePickerMode? = getDateMode()

    /**
     * Start the DatePicker dialog and display it on screen.
     *
     * @param dialogMode
     * @param ignoreFieldMode Whether the field should ignore state configured
     * with setDatePickerMode() or through attr.datePickerModes attribute in the XML.
     * If true, the dialog will show DatePicker with dialogMode mode.
     */
    fun showDatePickerDialog(
        dialogMode: DatePickerMode = DatePickerMode.DEFAULT,
        ignoreFieldMode: Boolean = false
    ) {
        showPickerDialog(dialogMode, ignoreFieldMode)
    }

    /**
     * Sets a listener to be invoked when the DatePicker dialog visibility is changing.
     */
    fun setDatePickerVisibilityChangeListener(l: OnDatePickerVisibilityChangeListener?) {
        setDatePickerVisibilityListener(l)
    }

    /**
     * It return current state of the field.
     *
     * @return current state.
     */
    fun getState(): FieldState.DateRangeState? {
        return getDateState()
    }

    /**
     * Sets output data serializers, which will serialize data before send it to back-end.
     *
     * @param serializer - FieldDataSerializer serializer.
     */
    fun setSerializer(serializer: FieldDataSerializer<*, *>?) {
        if (serializer == null) setSerializers(null) else setSerializers(listOf(serializer))
    }

    /**
     * Sets output data serializers, which will serialize data before send it to back-end.
     *
     * @param serializers - list of FieldDataSerializer serializers.
     */
    fun setSerializers(serializers: List<FieldDataSerializer<*, *>>?) {
        super.setFieldDataSerializers(serializers)
    }

    /**
     * Sets the vault alias format in which data stores on a backend.
     *
     * @param format The VGS alias format.
     */
    fun setVaultAliasFormat(format: VGSVaultAliasFormat) {
        applyAliasFormat(format)
    }

    /**
     * Sets the vault storage type for storing.
     *
     * @param storage The VGS storage type.
     */
    fun setVaultStorageType(storage: VGSVaultStorageType) {
        applyStorageType(storage)
    }

    /**
     * Defines if data requires tokenization.
     *
     * @param isEnabled Is tokenization enabled.
     */
    fun setEnabledTokenization(isEnabled: Boolean) {
        enableTokenization(isEnabled)
    }
}
package com.verygoodsecurity.vgscollect.widget.core

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
import com.verygoodsecurity.vgscollect.widget.ExpirationDateEditText

/**
 * Provides a user interface element for date input. The range of dates supported by this field is not configurable.
 *
 * @since 1.0.7
 */
abstract class DateEditText @JvmOverloads internal constructor(
    type: FieldType, context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : InputFieldView(context, attrs, defStyleAttr) {
    init {

        setupViewType(type)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.DateEditText,
            0, 0
        ).apply {

            try {
                val formatterMode = getInt(
                    R.styleable.DateEditText_formatterMode,
                    FormatMode.STRICT.ordinal
                )
                val datePattern = getString(R.styleable.DateEditText_datePattern)
                val outputPattern = getString(R.styleable.DateEditText_outputPattern)
                val datePickerMode = getInt(R.styleable.DateEditText_datePickerModes, 1)

                val inputType =
                    getInt(R.styleable.DateEditText_inputType, EditorInfo.TYPE_NULL)
                val fieldName = getString(R.styleable.DateEditText_fieldName)
                val hint = getString(R.styleable.DateEditText_hint)
                val textSize = getDimension(R.styleable.DateEditText_textSize, -1f)
                val textColor = getColor(R.styleable.DateEditText_textColor, Color.BLACK)
                val text = getString(R.styleable.DateEditText_text)
                val textStyle = getInt(R.styleable.DateEditText_textStyle, -1)
                val cursorVisible =
                    getBoolean(R.styleable.DateEditText_cursorVisible, true)
                val enabled = getBoolean(R.styleable.DateEditText_enabled, true)
                val isRequired = getBoolean(R.styleable.DateEditText_isRequired, true)
                val singleLine = getBoolean(R.styleable.DateEditText_singleLine, true)
                val scrollHorizontally =
                    getBoolean(R.styleable.DateEditText_scrollHorizontally, true)
                val gravity = getInt(
                    R.styleable.DateEditText_gravity,
                    Gravity.START or Gravity.CENTER_VERTICAL
                )
                val ellipsize = getInt(R.styleable.DateEditText_ellipsize, 0)
                val aliasFormat = getInt(
                    R.styleable.DateEditText_aliasFormat,
                    VGSVaultAliasFormat.UUID.ordinal
                )
                val storageType = getInt(
                    R.styleable.DateEditText_storageType,
                    VGSVaultStorageType.PERSISTENT.ordinal
                )
                val enableTokenization =
                    getBoolean(R.styleable.DateEditText_enableTokenization, true)

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

                setFormatterMode(formatterMode)
                setDatePickerMode(datePickerMode)
                setDateFormat(datePattern)
                setOutputFormat(outputPattern)

                setEnabledTokenization(enableTokenization)
                setVaultAliasFormat(VGSVaultAliasFormat.values()[aliasFormat])
                setVaultStorageType(VGSVaultStorageType.values()[storageType])
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
        setOutputFormat(regex)
    }

    /**
     * Representation of dates and times is an international standard covering the exchange of date- and time-related data.
     * The method uses the ISO 8601 standard.
     *
     * @param regex Specifies date representation format
     *
     */
    fun setDateRegex(regex: String) {
        setDateFormat(regex)
    }

    /**
     * Return regex date representation format.
     *
     * @return regex
     */
    fun getDateRegex(): String? = getDateFormat()

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
    fun setDatePickerVisibilityChangeListener(l: VisibilityChangeListener?) {
        setDatePickerVisibilityListener(l)
    }

    /**
     * It return current state of the field.
     *
     * @return current state.
     */
    fun getState(): FieldState.CardExpirationDateState? {
        return getExpirationDate()
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

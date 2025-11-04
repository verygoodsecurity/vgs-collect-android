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

/**
 * An abstract class for all VGS date input fields.
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
                isEnabled = enabled

                setInputType(inputType)

                setFormatterMode(formatterMode)
                setDatePickerMode(datePickerMode)
                setDatePattern(datePattern)
                setOutputPattern(outputPattern)

                setEnabledTokenization(enableTokenization)
                setVaultAliasFormat(VGSVaultAliasFormat.values()[aliasFormat])
                setVaultStorageType(VGSVaultStorageType.values()[storageType])
            } finally {
                recycle()
            }
        }
    }

    /**
     * Sets the date format for the output data.
     *
     * @param regex The date format regex.
     */
    fun setOutputRegex(regex: String) {
        setOutputPattern(regex)
    }

    /**
     * Sets the date format for the input field.
     *
     * @param regex The date format regex.
     */
    fun setDateRegex(regex: String) {
        setDatePattern(regex)
    }

    /**
     * Returns the date format regex.
     *
     * @return The date format regex.
     */
    fun getDateRegex(): String? = getDatePattern()

    /**
     * Sets the appearance and interaction mode of the date picker.
     *
     * @param mode The date picker mode.
     */
    fun setDatePickerMode(mode: DatePickerMode) {
        setDatePickerMode(mode.ordinal)
    }

    /**
     * Returns the appearance and interaction mode of the date picker.
     *
     * @return The date picker mode.
     */
    fun getDatePickerMode(): DatePickerMode? = getDateMode()

    /**
     * Shows the date picker dialog.
     *
     * @param dialogMode The date picker mode to use.
     * @param ignoreFieldMode Whether to ignore the field's configured date picker mode.
     */
    fun showDatePickerDialog(
        dialogMode: DatePickerMode = DatePickerMode.DEFAULT,
        ignoreFieldMode: Boolean = false
    ) {
        showPickerDialog(dialogMode, ignoreFieldMode)
    }

    /**
     * Sets a listener to be invoked when the date picker dialog's visibility changes.
     *
     * @param l The listener to be invoked.
     */
    fun setDatePickerVisibilityChangeListener(l: VisibilityChangeListener?) {
        setDatePickerVisibilityListener(l)
    }

    /**
     * Returns the current state of the field.
     *
     * @return The current state of the field.
     */
    fun getState(): FieldState.DateState? {
        return getDateState()
    }

    /**
     * Sets the output data serializer.
     *
     * @param serializer The serializer.
     */
    fun setSerializer(serializer: FieldDataSerializer<*, *>?) {
        if (serializer == null) setSerializers(null) else setSerializers(listOf(serializer))
    }

    /**
     * Sets a list of output data serializers.
     *
     * @param serializers The list of serializers.
     */
    fun setSerializers(serializers: List<FieldDataSerializer<*, *>>?) {
        super.setFieldDataSerializers(serializers)
    }

    /**
     * Sets the vault alias format in which data is stored on the backend.
     *
     * @param format The VGS alias format.
     */
    fun setVaultAliasFormat(format: VGSVaultAliasFormat) {
        applyAliasFormat(format)
    }

    /**
     * Sets the vault storage type.
     *
     * @param storage The VGS storage type.
     */
    fun setVaultStorageType(storage: VGSVaultStorageType) {
        applyStorageType(storage)
    }

    /**
     * Defines if data requires tokenization.
     *
     * @param isEnabled Whether tokenization is enabled.
     */
    fun setEnabledTokenization(isEnabled: Boolean) {
        enableTokenization(isEnabled)
    }
}

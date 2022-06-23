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

class SSNEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : InputFieldView(context, attrs, defStyleAttr) {

    init {
        setupViewType(FieldType.SSN)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SSNEditText,
            0, 0
        ).apply {
            try {
                val numberDivider: String = getString(R.styleable.SSNEditText_numberDivider) ?: DIVIDER
                val outputNumberDivider: String =
                    getString(R.styleable.SSNEditText_outputNumberDivider) ?: DIVIDER

                val fieldName: String? = getString(R.styleable.SSNEditText_fieldName)

                val inputType = getInt(R.styleable.SSNEditText_inputType, EditorInfo.TYPE_NULL)
                val hint = getString(R.styleable.SSNEditText_hint)
                val textSize = getDimension(R.styleable.SSNEditText_textSize, -1f)
                val textColor = getColor(R.styleable.SSNEditText_textColor, Color.BLACK)
                val text = getString(R.styleable.SSNEditText_text)
                val textStyle = getInt(R.styleable.SSNEditText_textStyle, -1)
                val cursorVisible = getBoolean(R.styleable.SSNEditText_cursorVisible, true)
                val enabled = getBoolean(R.styleable.SSNEditText_enabled, true)
                val isRequired = getBoolean(R.styleable.SSNEditText_isRequired, true)
                val singleLine = getBoolean(R.styleable.SSNEditText_singleLine, true)
                val scrollHorizontally =
                    getBoolean(R.styleable.SSNEditText_scrollHorizontally, true)
                val gravity = getInt(
                    R.styleable.SSNEditText_gravity,
                    Gravity.START or Gravity.CENTER_VERTICAL
                )
                val ellipsize = getInt(R.styleable.SSNEditText_ellipsize, 0)
                val aliasFormat = getInt(R.styleable.SSNEditText_aliasFormat, VGSVaultAliasFormat.UUID.ordinal)
                val storageType = getInt(R.styleable.SSNEditText_storageType, VGSVaultStorageType.PERSISTENT.ordinal)
                val enableTokenization = getBoolean(R.styleable.SSNEditText_enableTokenization, true)

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

                setNumberDivider(numberDivider)
                setOutputNumberDivider(outputNumberDivider)

                setEnabledTokenization(enableTokenization)
                setVaultAliasFormat(VGSVaultAliasFormat.values()[aliasFormat])
                setVaultStorageType(VGSVaultStorageType.values()[storageType])
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
    fun getState(): FieldState.SSNNumberState? {
        return getSSNState()
    }

    /**
     * Sets the symbol that will divide groups of digits in the number.
     * The divider make impact only on UI.
     * 000 00 0000
     *
     * @param char The divider symbol.
     */
    fun setDivider(char: Char?) {
        setNumberDivider(char?.toString())
    }

    /**
     * Return symbol that will divide groups of digits in the number.
     *
     * @return divider symbol
     */
    fun getDivider(): Char? {
        return getNumberDivider()
    }

    /**
     * Sets the symbol that will divide groups of digits in the number before submit it.
     * The divider has no impact on UI.
     * 000 00 0000
     *
     * @param char The divider symbol.
     */
    fun setOutputDivider(char: Char?) {
        setOutputNumberDivider(char?.toString())
    }

    /**
     * Return symbol that will divide groups of digits in the number before submitting on Proxy.
     *
     * @return divider symbol
     */
    fun getOutputDivider(): Char? {
        return getOutputNumberDivider()
    }

    /**
     * TODO: add description
     */
    fun setVaultAliasFormat(format: VGSVaultAliasFormat) {
        applyAliasFormat(format)
    }

    /**
     * TODO: add description
     */
    fun setVaultStorageType(storage: VGSVaultStorageType) {
        applyStorageType(storage)
    }

    /**
     * TODO: add description
     */
    fun setEnabledTokenization(isEnabled: Boolean) {
        enableTokenization(isEnabled)
    }

    companion object {
        internal val TAG: String = SSNEditText::class.simpleName.toString()

        internal const val DIVIDER = "-"
    }
}
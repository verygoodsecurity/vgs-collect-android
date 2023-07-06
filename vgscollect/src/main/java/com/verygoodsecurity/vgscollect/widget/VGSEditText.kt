package com.verygoodsecurity.vgscollect.widget

import android.content.Context
import android.graphics.Color
import android.text.InputType
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import android.view.inputmethod.EditorInfo
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultAliasFormat
import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultStorageType
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.validation.rules.VGSInfoRule

/**
 * A user interface element that displays text.
 *
 * @since 1.0.0
 */
open class VGSEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : InputFieldView(context, attrs, defStyleAttr) {

    init {
        setupViewType(FieldType.INFO)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.VGSEditText,
            0, 0
        ).apply {

            try {
                val inputType =
                    getInt(R.styleable.VGSEditText_inputType, EditorInfo.TYPE_CLASS_TEXT)
                val fieldName = getString(R.styleable.VGSEditText_fieldName)
                val hint = getString(R.styleable.VGSEditText_hint)
                val textSize = getDimension(R.styleable.VGSEditText_textSize, -1f)
                val textColor = getColor(R.styleable.VGSEditText_textColor, Color.BLACK)
                val text = getString(R.styleable.VGSEditText_text)
                val textStyle = getInt(R.styleable.VGSEditText_textStyle, -1)
                val cursorVisible = getBoolean(R.styleable.VGSEditText_cursorVisible, true)
                val enabled = getBoolean(R.styleable.VGSEditText_enabled, true)
                val isRequired = getBoolean(R.styleable.VGSEditText_isRequired, true)
                val singleLine = getBoolean(R.styleable.VGSEditText_singleLine, true)
                val scrollHorizontally =
                    getBoolean(R.styleable.VGSEditText_scrollHorizontally, true)
                val gravity = getInt(
                    R.styleable.VGSEditText_gravity,
                    Gravity.START or Gravity.CENTER_VERTICAL
                )
                val ellipsize = getInt(R.styleable.VGSEditText_ellipsize, 0)

                val minLines = getInt(R.styleable.VGSEditText_minLines, 0)
                val maxLines = getInt(R.styleable.VGSEditText_maxLines, 0)
                val aliasFormat =
                    getInt(R.styleable.VGSEditText_aliasFormat, VGSVaultAliasFormat.UUID.ordinal)
                val storageType = getInt(
                    R.styleable.VGSEditText_storageType,
                    VGSVaultStorageType.PERSISTENT.ordinal
                )
                val enableTokenization =
                    getBoolean(R.styleable.VGSEditText_enableTokenization, true)
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
                setVaultAliasFormat(VGSVaultAliasFormat.values()[aliasFormat])
                setVaultStorageType(VGSVaultStorageType.values()[storageType])
                enableTokenization(enableTokenization)

                setInputType(inputType)
            } finally {
                recycle()
            }
        }
    }

    override fun setInputType(inputType: Int) {
        if (inputType == InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS) {
            super.setInputType(getInputType() or inputType)
            return
        }
        super.setInputType(inputType)
    }

    /**
     * It return current state of the field.
     *
     * @return current state.
     */
    fun getState(): FieldState.InfoState? {
        return getInfoState()
    }

    /** The max text length to display. */
    fun setMaxLength(length: Int) {
        applyMaxLength(length)
    }

    /**
     * Adds a validation rule for the field.
     */
    @Deprecated("Use setRule(rule) instead.", ReplaceWith("setRule(rule)"))
    fun addRule(rule: VGSInfoRule) {
        applyValidationRule(rule)
    }

    /**
     * Set a validation rule for the field.
     */
    fun setRule(rule: VGSInfoRule) {
        applyValidationRule(rule)
    }

    /**
     * Set validation rules for the field.
     */
    fun setRules(rules: List<VGSInfoRule>) {
        applyValidationRules(rules)
    }

    /**
     * Adds a validation rule for the field.
     */
    fun appendRule(rule: VGSInfoRule) {
        appendValidationRule(rule)
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
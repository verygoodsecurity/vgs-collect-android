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
import com.verygoodsecurity.vgscollect.view.card.validation.rules.PersonNameRule

/**
 * A user interface element for inputting a person's name, typically a cardholder name.
 */
class PersonNameEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : InputFieldView(context, attrs, defStyleAttr) {

    init {
        setupViewType(FieldType.CARD_HOLDER_NAME)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PersonNameEditText,
            0, 0
        ).apply {

            try {
                val inputType =
                    getInt(R.styleable.PersonNameEditText_inputType, EditorInfo.TYPE_NULL)
                val fieldName = getString(R.styleable.PersonNameEditText_fieldName)
                val hint = getString(R.styleable.PersonNameEditText_hint)
                val textSize = getDimension(R.styleable.PersonNameEditText_textSize, -1f)
                val textColor = getColor(R.styleable.PersonNameEditText_textColor, Color.BLACK)
                val text = getString(R.styleable.PersonNameEditText_text)
                val textStyle = getInt(R.styleable.PersonNameEditText_textStyle, -1)
                val cursorVisible = getBoolean(R.styleable.PersonNameEditText_cursorVisible, true)
                val enabled = getBoolean(R.styleable.PersonNameEditText_enabled, true)
                val isRequired = getBoolean(R.styleable.PersonNameEditText_isRequired, true)
                val singleLine = getBoolean(R.styleable.PersonNameEditText_singleLine, true)
                val scrollHorizontally =
                    getBoolean(R.styleable.PersonNameEditText_scrollHorizontally, true)
                val gravity = getInt(
                    R.styleable.PersonNameEditText_gravity,
                    Gravity.START or Gravity.CENTER_VERTICAL
                )
                val ellipsize = getInt(R.styleable.PersonNameEditText_ellipsize, 0)

                val minLines = getInt(R.styleable.PersonNameEditText_minLines, 0)
                val maxLines = getInt(R.styleable.PersonNameEditText_maxLines, 0)
                val aliasFormat = getInt(R.styleable.PersonNameEditText_aliasFormat, VGSVaultAliasFormat.UUID.ordinal)
                val storageType = getInt(R.styleable.PersonNameEditText_storageType, VGSVaultStorageType.PERSISTENT.ordinal)
                val enableTokenization = getBoolean(R.styleable.PersonNameEditText_enableTokenization, true)

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
                setTypeface(getTypeface(), textStyle)

                setText(text)
                isEnabled = enabled

                setInputType(inputType)

                setEnabledTokenization(enableTokenization)
                setVaultAliasFormat(VGSVaultAliasFormat.values()[aliasFormat])
                setVaultStorageType(VGSVaultStorageType.values()[storageType])
            } finally {
                recycle()
            }
        }
    }

    /**
     * Returns the current state of the field.
     *
     * @return The current state of the field.
     */
    fun getState(): FieldState.CardHolderNameState? {
        return getCardHolderName()
    }

    /**
     * Adds a validation rule for the field.
     *
     * @param rule The validation rule to add.
     */
    @Deprecated("Use setRule(rule) instead.", ReplaceWith("setRule(rule)"))
    fun addRule(rule: PersonNameRule) {
        applyValidationRule(rule)
    }

    /**
     * Sets a validation rule for the field.
     *
     * @param rule The validation rule to set.
     */
    fun setRule(rule: PersonNameRule) {
        applyValidationRule(rule)
    }

    /**
     * Sets a list of validation rules for the field.
     *
     * @param rules The list of validation rules to set.
     */
    fun setRules(rules: List<PersonNameRule>) {
        applyValidationRules(rules)
    }

    /**
     * Appends a validation rule to the field.
     *
     * @param rule The validation rule to append.
     */
    fun appendRule(rule: PersonNameRule) {
        appendValidationRule(rule)
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
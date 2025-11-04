package com.verygoodsecurity.vgscollect.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.VGSCollectLogger
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultAliasFormat
import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultStorageType
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.card.BrandParams
import com.verygoodsecurity.vgscollect.view.card.CardBrand
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.formatter.CardMaskAdapter
import com.verygoodsecurity.vgscollect.view.card.icon.CardIconAdapter
import com.verygoodsecurity.vgscollect.view.card.validation.payment.ChecksumAlgorithm
import com.verygoodsecurity.vgscollect.view.card.validation.rules.PaymentCardNumberRule

/**
 * A user interface element for inputting a card number.
 */
class VGSCardNumberEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : InputFieldView(context, attrs, defStyleAttr) {


    init {
        setupViewType(FieldType.CARD_NUMBER)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.VGSCardNumberEditText,
            0, 0
        ).apply {

            try {
                val previewGravity =
                    getInt(R.styleable.VGSCardNumberEditText_cardBrandIconGravity, 0)
                val brandIconVisibility =
                    getInt(R.styleable.VGSCardNumberEditText_brandIconVisibility, 0)

                val divider: String? = getString(R.styleable.VGSCardNumberEditText_numberDivider)
                val outputNumberDivider: String? =
                    getString(R.styleable.VGSCardNumberEditText_outputNumberDivider)

                val inputType =
                    getInt(R.styleable.VGSCardNumberEditText_inputType, EditorInfo.TYPE_NULL)
                val fieldName = getString(R.styleable.VGSCardNumberEditText_fieldName)
                val hint = getString(R.styleable.VGSCardNumberEditText_hint)
                val textSize = getDimension(R.styleable.VGSCardNumberEditText_textSize, -1f)
                val textColor = getColor(R.styleable.VGSCardNumberEditText_textColor, Color.BLACK)
                val text = getString(R.styleable.VGSCardNumberEditText_text)
                val textStyle = getInt(R.styleable.VGSCardNumberEditText_textStyle, -1)
                val cursorVisible =
                    getBoolean(R.styleable.VGSCardNumberEditText_cursorVisible, true)
                val enabled = getBoolean(R.styleable.VGSCardNumberEditText_enabled, true)
                val isRequired = getBoolean(R.styleable.VGSCardNumberEditText_isRequired, true)
                val singleLine = getBoolean(R.styleable.VGSCardNumberEditText_singleLine, true)
                val scrollHorizontally =
                    getBoolean(R.styleable.VGSCardNumberEditText_scrollHorizontally, true)
                val gravity = getInt(
                    R.styleable.VGSCardNumberEditText_gravity,
                    Gravity.START or Gravity.CENTER_VERTICAL
                )
                val ellipsize = getInt(R.styleable.VGSCardNumberEditText_ellipsize, 0)

                val minLines = getInt(R.styleable.VGSCardNumberEditText_minLines, 0)
                val maxLines = getInt(R.styleable.VGSCardNumberEditText_maxLines, 0)
                val validationRule = getInt(R.styleable.VGSCardNumberEditText_validationRule, 0)
                val aliasFormat = getInt(
                    R.styleable.VGSCardNumberEditText_aliasFormat,
                    VGSVaultAliasFormat.FPE_SIX_T_FOUR.ordinal
                )
                val storageType = getInt(
                    R.styleable.VGSCardNumberEditText_storageType,
                    VGSVaultStorageType.PERSISTENT.ordinal
                )

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
                isEnabled = enabled

                setInputType(inputType)

                setVaultAliasFormat(VGSVaultAliasFormat.values()[aliasFormat])
                applyStorageType(VGSVaultStorageType.values()[storageType])

                setNumberDivider(divider)
                setOutputNumberDivider(outputNumberDivider)
                applyPreviewIconGravity(previewGravity)
                applyPreviewIconMode(brandIconVisibility)

                if (!isValidationPredefined()) {
                    predefineValidationRule(validationRule)
                }
            } finally {
                recycle()
            }
        }
    }

    private fun predefineValidationRule(validationRule: Int) {
        when (validationRule) {
            0 -> enableValidation(true)
            1 -> setupValidationRules()
            2 -> enableValidation(false)
        }
    }

    private fun setupValidationRules() {
        val rule: PaymentCardNumberRule = PaymentCardNumberRule.ValidationBuilder()
            .setAlgorithm(ChecksumAlgorithm.LUHN)
            .setAllowableMinLength(16)
            .setAllowableMaxLength(19)
            .build()
        setRule(rule)
    }

    /**
     * Specifies how to align the icon by the view’s x-axis. To specify gravity programmatically
     * you could use android Gravity class.
     *
     * @param gravity Specifies how to align the icon by the view’s x-axis.
     */
    fun setCardBrandIconGravity(gravity: Int) {
        applyPreviewIconGravity(gravity)
    }

    /**
     * Returns the icon alignment by the view’s x-axis.
     *
     * @return The icon gravity.
     */
    fun getCardPreviewIconGravity(): Int {
        return getCardIconGravity()
    }

    /**
     * Adds a new card brand to the list of supported brands or overrides an existing one.
     *
     * @param c The new card brand definition.
     */
    fun addCardBrand(c: CardBrand) {
        val digitCount = c.params.mask.replace("[^#]".toRegex(), "").length
        if (c.params.rangeNumber.contains(digitCount)) {
            applyCardBrand(c)
        } else {
            VGSCollectLogger.warn(
                BrandParams::class.qualifiedName.toString(),
                context.getString(R.string.error_custom_brand_mask_length, c.cardBrandName)
            )
        }
    }

    /**
     * Modifies the list of valid card brands. Only the brands in this list will be considered valid.
     * The order of the brands in the list is important for correct brand detection.
     *
     * @param cardBrand The list of valid card brands.
     */
    fun setValidCardBrands(vararg cardBrand: CardBrand) {
        super.setValidCardBrands(cardBrand.toList())
    }

    /**
     * Sets the symbol that will divide groups of digits in the card number.
     * 0000 0000 0000 0000
     *
     * @param char The divider symbol.
     */
    fun setDivider(char: Char?) {
        setNumberDivider(char?.toString())
    }

    /**
     * Returns the symbol that divides groups of digits in the card number.
     *
     * @return The divider symbol.
     */
    fun getDivider(): Char? {
        return getNumberDivider()
    }

    /**
     * Sets the symbol that will divide groups of digits in the card number before it is submitted.
     * This divider does not affect the UI.
     *
     * @param char The divider symbol.
     */
    fun setOutputDivider(char: Char?) {
        setOutputNumberDivider(char?.toString())
    }

    /**
     * Returns the symbol that will divide groups of digits in the card number before it is submitted to the proxy.
     *
     * @return The divider symbol.
     */
    fun getOutputDivider(): Char? {
        return getOutputNumberDivider()
    }

    /**
     * Sets the maximum length of the card number.
     * Divider characters are not counted.
     *
     * @param length The maximum length of the card number.
     */
    fun setMaxInputLength(length: Int) {
        applyMaxLength(length)
    }

    /**
     * Sets a custom icon adapter for the card brand icons.
     *
     * @param adapter The icon adapter.
     */
    fun setCardIconAdapter(adapter: CardIconAdapter?) {
        setCardBrandIconAdapter(adapter)
    }

    /**
     * Sets a custom mask adapter for formatting the card number.
     *
     * @param adapter The mask adapter.
     */
    fun setCardMaskAdapter(adapter: CardMaskAdapter) {
        setCardBrandMaskAdapter(adapter)
    }

    /**
     * Returns the current state of the field.
     *
     * @return The current state of the field.
     */
    fun getState(): FieldState.CardNumberState? {
        return getCardNumberState()
    }

    /**
     * Adds a validation rule for the field.
     *
     * @param rule The validation rule to add.
     */
    @Deprecated("Use setRule(rule) instead.", ReplaceWith("setRule(rule)"))
    fun addRule(rule: PaymentCardNumberRule) {
        applyValidationRule(rule)
    }

    /**
     * Sets a validation rule for the field.
     *
     * @param rule The validation rule to set.
     */
    fun setRule(rule: PaymentCardNumberRule) {
        applyValidationRule(rule)
    }

    /**
     * Sets a list of validation rules for the field.
     *
     * @param rules The list of validation rules to set.
     */
    fun setRules(rules: List<PaymentCardNumberRule>) {
        applyValidationRules(rules)
    }

    /**
     * Appends a validation rule to the field.
     *
     * @param rule The validation rule to append.
     */
    fun appendRule(rule: PaymentCardNumberRule) {
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
     * @param type The VGS storage type.
     */
    fun setVaultStorageType(type: VGSVaultStorageType) {
        applyStorageType(type)
    }


    companion object {
        internal val TAG: String = VGSCardNumberEditText::class.simpleName.toString()
    }
}
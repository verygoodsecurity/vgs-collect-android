package com.verygoodsecurity.vgscollect.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.VGSCollectLogger
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.card.BrandParams
import com.verygoodsecurity.vgscollect.view.card.CardBrand
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.validation.rules.PaymentCardNumberRule
import com.verygoodsecurity.vgscollect.view.card.formatter.CardMaskAdapter
import com.verygoodsecurity.vgscollect.view.card.icon.CardIconAdapter
import com.verygoodsecurity.vgscollect.view.card.validation.payment.ChecksumAlgorithm

/**
 * A user interface element that displays text to the user in bank card number format.
 *
 * @since 1.0.2
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

        addRule(rule)
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
     * Return the align the icon by the view’s x-axis.
     *
     * @return the icon gravity value.
     */
    fun getCardPreviewIconGravity(): Int {
        return getCardIconGravity()
    }

    /**
     * It may be useful to add new brands in addition to already defined brands or override existing ones.
     *
     * @param c new card definition
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
     * Sets the symbol that will divide groups of digits in the card number.
     * 0000 0000 0000 0000
     *
     * @param char The divider symbol.
     */
    fun setDivider(char: Char?) {
        setNumberDivider(char.toString())
    }

    /**
     * Return symbol that will divide groups of digits in the card number.
     *
     * @return divider symbol
     */
    fun getDivider(): Char? {
        return getNumberDivider()
    }

    /**
     * Sets the symbol that will divide groups of digits in the card number before submit it.
     * The divider has no impact on UI.
     * 0000 0000 0000 0000
     *
     * @param char The divider symbol.
     */
    fun setOutputDivider(char: Char?) {
        setOutputNumberDivider(char?.toString())
    }

    /**
     * Return symbol that will divide groups of digits in the card number before submitting on Proxy.
     *
     * @return divider symbol
     */
    fun getOutputDivider(): Char? {
        return getOutputNumberDivider()
    }

    /**
     * Sets the custom icons for Brand.
     *
     * @param adapter The adapter is responsible for maintaining the icons backing this view and
     * for producing a drawable for preview.
     */
    fun setCardIconAdapter(adapter: CardIconAdapter?) {
        setCardBrandIconAdapter(adapter)
    }

    /**
     * Sets the custom mask for formatting card number.
     *
     * @param adapter The adapter is responsible for maintaining the format of the card number.
     */
    fun setCardMaskAdapter(adapter: CardMaskAdapter) {
        setCardBrandMaskAdapter(adapter)
    }

    /**
     * It return current state of the field.
     *
     * @return current state.
     */
    fun getState(): FieldState.CardNumberState? {
        return getCardNumberState()
    }

    /**
     * Adds a behaviour rule for the field.
     */
    fun addRule(rule: PaymentCardNumberRule) {
        applyValidationRule(rule)
    }

    companion object {
        internal val TAG: String = VGSCardNumberEditText::class.simpleName.toString()
    }
}
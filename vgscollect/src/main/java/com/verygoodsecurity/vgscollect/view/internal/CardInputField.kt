package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.graphics.drawable.Drawable
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.Gravity
import android.view.View
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.core.model.state.*
import com.verygoodsecurity.vgscollect.VGSCollectLogger
import com.verygoodsecurity.vgscollect.util.extension.isNumeric
import com.verygoodsecurity.vgscollect.view.card.*
import com.verygoodsecurity.vgscollect.view.card.conection.InputCardNumberConnection
import com.verygoodsecurity.vgscollect.view.card.filter.CardBrandFilter
import com.verygoodsecurity.vgscollect.view.card.filter.CardBrandPreview
import com.verygoodsecurity.vgscollect.view.card.filter.DefaultCardBrandFilter
import com.verygoodsecurity.vgscollect.view.card.filter.MutableCardFilter
import com.verygoodsecurity.vgscollect.view.card.formatter.CardMaskAdapter
import com.verygoodsecurity.vgscollect.view.card.formatter.CardNumberFormatter
import com.verygoodsecurity.vgscollect.view.card.formatter.Formatter
import com.verygoodsecurity.vgscollect.view.card.icon.CardIconAdapter
import com.verygoodsecurity.vgscollect.view.card.validation.CheckSumValidator
import com.verygoodsecurity.vgscollect.view.card.validation.LengthValidator
import com.verygoodsecurity.vgscollect.view.card.validation.MutableValidator
import com.verygoodsecurity.vgscollect.view.card.validation.CompositeValidator
import com.verygoodsecurity.vgscollect.view.card.validation.rules.PaymentCardNumberRule
import com.verygoodsecurity.vgscollect.widget.VGSCardNumberEditText

/** @suppress */
internal class CardInputField(context: Context) : BaseInputField(context), InputCardNumberConnection.IDrawCardBrand {

    companion object {
        private const val MASK_REGEX = "[^#]"
        private const val DEFAULT_MASK = "#### #### #### #### ###"
        private const val EMPTY_CHAR = ""
        private const val SPACE = " "
    }

    override var fieldType: FieldType = FieldType.CARD_NUMBER

    private var divider: String = SPACE
    private var iconGravity: Int = Gravity.NO_GRAVITY
    private var cardtype: CardType = CardType.UNKNOWN

    private var cardNumberMask: String = DEFAULT_MASK

    private var iconAdapter = CardIconAdapter(context)
    private var previewIconMode: PreviewIconMode = PreviewIconMode.ALWAYS

    private var maskAdapter = CardMaskAdapter()
    private var cardNumberFormatter: Formatter? = null

    private val userFilter: MutableCardFilter by lazy {
        CardBrandFilter(divider)
    }

    enum class PreviewIconMode {
        ALWAYS,
        IF_DETECTED,
        HAS_CONTENT,
        NEVER
    }

    override fun applyFieldType() {
        inputConnection = InputCardNumberConnection(id, validator, this, divider).apply {
            this.regexValidator = this@CardInputField.regexValidator
        }

        val defFilter = DefaultCardBrandFilter(CardType.values(), divider)
        inputConnection!!.addFilter(defFilter)
        inputConnection!!.addFilter(userFilter)

        val str = text.toString()
        val stateContent = FieldContent.CardNumberContent().apply {
            rawData = str.replace(divider, EMPTY_CHAR)
            cardtype = this@CardInputField.cardtype
            this.data = str
        }
        val state = collectCurrentState(stateContent)

        inputConnection?.setOutput(state)
        inputConnection?.setOutputListener(stateListener)

        applyFormatter()
        applyInputType()
    }

    private fun applyFormatter() {
        cardNumberFormatter = with(CardNumberFormatter()) {
            setMask(cardNumberMask)
            applyNewTextWatcher(this)
            this
        }
    }

    private fun applyInputType() {
        if (!isValidInputType(inputType)) {
            inputType = InputType.TYPE_CLASS_NUMBER
        }
        refreshInput()
    }

    private fun isValidInputType(type: Int): Boolean {
        return type == InputType.TYPE_CLASS_NUMBER ||
                type == InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
    }

    override fun updateTextChanged(str: String) {
        inputConnection?.also {
            with(it.getOutput()) {
                if (str.isNotEmpty()) {
                    hasUserInteraction = true
                }
                content = createCardNumberContent(str)
            }
            it.run()
        }
    }

    private fun createCardNumberContent(str: String): FieldContent.CardNumberContent {
        val c = FieldContent.CardNumberContent()
        c.cardtype = this@CardInputField.cardtype
        c.rawData = str.replace(divider, EMPTY_CHAR)
        c.data = str
        return c
    }

    internal fun setPreviewIconMode(mode: Int) {
        previewIconMode = PreviewIconMode.values()[mode]
        refreshIconPreview()
    }

    internal fun setCardPreviewIconGravity(gravity: Int) {
        iconGravity = when (gravity) {
            0 -> gravity
            Gravity.RIGHT -> gravity
            Gravity.LEFT -> gravity
            Gravity.START -> gravity
            Gravity.END -> gravity
            else -> Gravity.END
        }
        refreshIconPreview()
    }

    internal fun getCardPreviewIconGravity(): Int {
        return iconGravity
    }

    internal fun setCardBrand(c: CardBrand) {
        userFilter.add(c)
        inputConnection?.run()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (isRTL()) {
            hasRTL = true
            layoutDirection = View.LAYOUT_DIRECTION_LTR
            textDirection = View.TEXT_DIRECTION_LTR
            gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
        }
    }

    internal fun setNumberDivider(divider: String?) {
        when {
            divider.isNullOrEmpty() -> this@CardInputField.divider = EMPTY_CHAR
            divider.isNumeric() -> printErrorInLog(R.string.error_divider_card_number_field)
            divider.length > 1 -> printErrorInLog(R.string.error_divider_count_card_number_field)
            else -> this@CardInputField.divider = divider
        }

        applyDividerOnMask()
        setupKeyListener()
        refreshInputConnection()
    }

    private fun printErrorInLog(resId: Int) {
        VGSCollectLogger.warn(VGSCardNumberEditText.TAG, context.getString(resId))
    }

    private fun setupKeyListener() {
        val digits = resources.getString(R.string.card_number_digits) + this@CardInputField.divider
        keyListener = DigitsKeyListener.getInstance(digits)
    }

    internal fun getNumberDivider() = divider

    override fun setInputType(type: Int) {
        val validType = validateInputType(type)
        super.setInputType(validType)
        refreshInput()
    }

    private fun validateInputType(type: Int): Int {
        return when (type) {
            InputType.TYPE_CLASS_NUMBER -> type
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_NUMBER_VARIATION_PASSWORD -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            InputType.TYPE_NUMBER_VARIATION_PASSWORD -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD -> type
            else -> InputType.TYPE_CLASS_NUMBER
        }
    }

    internal fun setCardBrandAdapter(adapter: CardIconAdapter?) {
        iconAdapter = adapter ?: CardIconAdapter(context)
    }

    internal fun setCardBrandMaskAdapter(adapter: CardMaskAdapter?) {
        maskAdapter = adapter ?: CardMaskAdapter()
    }

    private var lastCardIconPreview: Drawable? = null
    override fun onCardBrandPreview(card: CardBrandPreview) {
        this.cardtype = card.cardType
        updateMask(card)

        val r = Rect()
        getLocalVisibleRect(r)

        lastCardIconPreview = iconAdapter.getItem(card.cardType, card.name, card.resId, r)

        when(previewIconMode) {
            PreviewIconMode.ALWAYS -> refreshIconPreview()
            PreviewIconMode.IF_DETECTED -> if(card.successfullyDetected) {
                refreshIconPreview()
            } else {
                setCompoundDrawables(null, null, null, null)
            }
            PreviewIconMode.HAS_CONTENT -> if(!text.isNullOrEmpty()) {
                refreshIconPreview()
            } else {
                setCompoundDrawables(null, null, null, null)
            }
            PreviewIconMode.NEVER -> setCompoundDrawables(null, null, null, null)
        }
    }

    private fun refreshIconPreview() {
        when (iconGravity) {
            Gravity.LEFT -> setCompoundDrawables(lastCardIconPreview, null, null, null)
            Gravity.START -> setCompoundDrawables(lastCardIconPreview, null, null, null)
            Gravity.RIGHT -> setCompoundDrawables(null, null, lastCardIconPreview, null)
            Gravity.END -> setCompoundDrawables(null, null, lastCardIconPreview, null)
            Gravity.NO_GRAVITY -> setCompoundDrawables(null, null, null, null)
        }
    }

    private fun updateMask(
            card: CardBrandPreview
    ) {
        if (!text.isNullOrEmpty()) {
            val bin = (inputConnection?.getOutput()?.content as FieldContent.CardNumberContent).parseCardBin()
            cardNumberMask = maskAdapter.getItem(
                    card.cardType,
                    card.name ?: "",
                    bin,
                    card.currentMask)
            applyDividerOnMask()
        }
    }

    override fun setCompoundDrawables(
            left: Drawable?,
            top: Drawable?,
            right: Drawable?,
            bottom: Drawable?
    ) {
        if (hasRTL) {
            super.setCompoundDrawables(right, top, left, bottom)
        } else {
            super.setCompoundDrawables(left, top, right, bottom)
        }
    }

    private fun applyDividerOnMask() {
        cardNumberMask = with(cardNumberMask) {
            this.replace(Regex(MASK_REGEX), divider)
        }
        if (!text.isNullOrEmpty() && cardNumberFormatter?.getMask() != cardNumberMask) {
            cardNumberFormatter?.setMask(cardNumberMask)
            refreshInput()
        }
    }

    override fun setupAutofill() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setAutofillHints(AUTOFILL_HINT_CREDIT_CARD_NUMBER)
        }
    }

    private var validator: MutableValidator = CompositeValidator()

    internal fun applyValidationRule(rule: PaymentCardNumberRule) {
        validator.clearRules()
        rule.length?.let {
            validator.addRule(LengthValidator(it))
        }
        rule.algorithm?.let {
            validator.addRule(CheckSumValidator(rule.algorithm))
        }
    }
}
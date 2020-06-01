package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.Gravity
import android.view.View
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.util.Logger
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.card.*
import com.verygoodsecurity.vgscollect.view.card.filter.CardBrandFilter
import com.verygoodsecurity.vgscollect.view.card.filter.DefaultCardBrandFilter
import com.verygoodsecurity.vgscollect.view.card.filter.MutableCardFilter
import com.verygoodsecurity.vgscollect.view.card.icon.CardIconAdapter
import com.verygoodsecurity.vgscollect.view.card.text.CardNumberTextWatcher
import com.verygoodsecurity.vgscollect.view.card.validation.card.CardNumberValidator

/** @suppress */
internal class CardInputField(context: Context): BaseInputField(context), InputCardNumberConnection.IDrawCardBrand {

    override var fieldType: FieldType = FieldType.CARD_NUMBER

    private var divider:String? = " "
    private var iconGravity:Int = Gravity.NO_GRAVITY
    private var cardtype: CardType = CardType.NONE

    private var iconAdapter = CardIconAdapter(context)

    private val userFilter: MutableCardFilter by lazy {
        CardBrandFilter( this, divider)
    }

    override fun applyFieldType() {
        val validator = CardNumberValidator(divider)

        inputConnection = InputCardNumberConnection(id, validator, this, divider)

        val defFilter = DefaultCardBrandFilter(CardType.values(), this, divider)
        inputConnection!!.addFilter(defFilter)
        inputConnection!!.addFilter(userFilter)

        val str = text.toString()
        val stateContent = FieldContent.CardNumberContent().apply {
            rawData = str.replace(divider?:" ", "")
            cardtype = this@CardInputField.cardtype
            this.data = str
        }
        val state = collectCurrentState(stateContent)

        inputConnection?.setOutput(state)
        inputConnection?.setOutputListener(stateListener)
        applyNewTextWatcher(CardNumberTextWatcher(divider))

        applyInputType()
    }

    private fun applyInputType() {
        if(!isValidInputType(inputType)) {
            inputType = InputType.TYPE_CLASS_NUMBER
        }
        refreshInput()
    }

    private fun isValidInputType(type: Int):Boolean {
        return type == InputType.TYPE_CLASS_NUMBER ||
                type == InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
    }

    override fun updateTextChanged(str: String) {
        inputConnection?.getOutput()?.apply {
            if(str.isNotEmpty()) {
                hasUserInteraction = true
            }
            content = createCardNumberContent(str)

            handlerLooper.removeCallbacks(inputConnection)
            handlerLooper.postDelayed(inputConnection, 200)
        }
    }

    private fun createCardNumberContent(str: String): FieldContent.CardNumberContent {
        val c = FieldContent.CardNumberContent()
        c.cardtype = this@CardInputField.cardtype
        c.rawData = str.replace(divider?:" ", "")
        c.data = str
        return c
    }

    internal fun setCardPreviewIconGravity(gravity:Int) {
        iconGravity = when(gravity) {
            0 -> gravity
            Gravity.RIGHT -> gravity
            Gravity.LEFT -> gravity
            Gravity.START -> gravity
            Gravity.END -> gravity
            else -> Gravity.END
        }
    }

    internal fun getCardPreviewIconGravity():Int {
        return iconGravity
    }

    internal fun setCardBrand(c: CustomCardBrand) {
        userFilter.add(c)
        inputConnection?.run()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if(isRTL()) {
            hasRTL = true
            layoutDirection = View.LAYOUT_DIRECTION_LTR
            textDirection = View.TEXT_DIRECTION_LTR
            gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
        }
    }

    internal fun setNumberDivider(divider: String?) {
        when {
            divider.isNullOrEmpty() -> this@CardInputField.divider = ""
            divider.length == 1 -> this@CardInputField.divider = divider
            else -> {
                val message = String.format(
                    context.getString(R.string.error_divider_card_number_field),
                    divider
                )
                Logger.e(InputFieldView::class.java, message)
            }
        }

        val digits = resources.getString(R.string.card_number_digits) + this@CardInputField.divider
        keyListener = DigitsKeyListener.getInstance(digits)
        refreshInputConnection()
    }

    internal fun getNumberDivider() = divider

    override fun setInputType(type: Int) {
        val validType = validateInputType(type)
        super.setInputType(validType)
        refreshInput()
    }

    private fun validateInputType(type: Int):Int {
        return when(type) {
            InputType.TYPE_CLASS_NUMBER -> type
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_NUMBER_VARIATION_PASSWORD -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            InputType.TYPE_NUMBER_VARIATION_PASSWORD -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD -> type
            else -> InputType.TYPE_CLASS_NUMBER
        }
    }

    internal fun setCardBrandAdapter(adapter: CardIconAdapter) {
        iconAdapter = adapter
    }

    override fun drawCardBrandPreview(cardType: CardType, name: String?, resId: Int) {
        val r = Rect()
        getLocalVisibleRect(r)

        val cardPreview = iconAdapter.getItem(cardType, name, resId, r)

        when (iconGravity) {
            Gravity.LEFT -> setCompoundDrawables(cardPreview,null,null,null)
            Gravity.START -> setCompoundDrawables(cardPreview,null,null,null)
            Gravity.RIGHT -> setCompoundDrawables(null,null,cardPreview,null)
            Gravity.END -> setCompoundDrawables(null,null,cardPreview,null)
        }
    }

    override fun setCompoundDrawables(
        left: Drawable?,
        top: Drawable?,
        right: Drawable?,
        bottom: Drawable?
    ) {
        if(hasRTL) {
            super.setCompoundDrawables(right, top, left, bottom)
        } else {
            super.setCompoundDrawables(left, top, right, bottom)
        }
    }
}
package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.Gravity
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.util.Logger
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.card.*
import com.verygoodsecurity.vgscollect.view.card.filter.CardBrandFilter
import com.verygoodsecurity.vgscollect.view.card.filter.DefaultCardBrandFilter
import com.verygoodsecurity.vgscollect.view.card.filter.MutableCardFilter
import com.verygoodsecurity.vgscollect.view.card.text.CardNumberTextWatcher
import com.verygoodsecurity.vgscollect.view.card.validation.card.CardNumberValidator

/** @suppress */
internal class CardInputField(context: Context): BaseInputField(context) {

    override var fieldType: FieldType = FieldType.CARD_NUMBER

    private var divider:String? = " "
    private var iconGravity:Int = Gravity.NO_GRAVITY
    private var cardtype: CardType = CardType.NONE

    private val userFilter: MutableCardFilter by lazy {
        CardBrandFilter( this, divider)
    }

    override fun applyFieldType() {
        val validator = CardNumberValidator(divider)

        inputConnection = InputCardNumberConnection(id,
            validator,
            object :
                InputCardNumberConnection.IdrawCardBrand {
                override fun drawCardBrandPreview() {
                    this@CardInputField.drawCardBrandPreview()
                }
            },
            divider)

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

    private fun drawCardBrandPreview() {
        val state = inputConnection?.getOutput()

        var l: Drawable? = null
        var r: Drawable? = null

        val privaryRes = (state?.content as? FieldContent.CardNumberContent)?.iconResId

        when (iconGravity) {
            Gravity.LEFT -> l = getDrawable(privaryRes)
            Gravity.START -> l = getDrawable(privaryRes)
            Gravity.RIGHT -> r = getDrawable(privaryRes)
            Gravity.END -> r = getDrawable(privaryRes)
        }

        setCompoundDrawables(l,null,r,null)
    }

    private fun getDrawable(primaryRes:Int?): Drawable? {
        return if(primaryRes!= null && primaryRes != 0) {
            val c_icon_size_h = resources.getDimension(R.dimen.c_icon_size_h).toInt()
            val c_icon_size_w = resources.getDimension(R.dimen.c_icon_size_w).toInt()
            val drawable = ContextCompat.getDrawable(context, primaryRes)
            drawable?.setBounds(0, 0, c_icon_size_w, c_icon_size_h)
            drawable
        } else {
            null
        }
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                layoutDirection = View.LAYOUT_DIRECTION_LTR
                textDirection = View.TEXT_DIRECTION_LTR
            }
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
}
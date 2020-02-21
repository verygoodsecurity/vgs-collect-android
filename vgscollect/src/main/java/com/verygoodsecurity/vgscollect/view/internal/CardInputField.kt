package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.InputType
import android.view.Gravity
import android.view.View
import androidx.core.content.ContextCompat
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.util.Logger
import com.verygoodsecurity.vgscollect.view.card.*
import com.verygoodsecurity.vgscollect.view.card.filter.CardBrandFilter
import com.verygoodsecurity.vgscollect.view.card.filter.DefaultCardBrandFilter
import com.verygoodsecurity.vgscollect.view.card.filter.MutableCardFilter
import com.verygoodsecurity.vgscollect.view.card.text.CardNumberTextWatcher
import com.verygoodsecurity.vgscollect.view.card.validation.card.CardNumberValidator

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
            cardtype = this@CardInputField.cardtype
            this.data = str
        }
        val state = collectCurrentState(stateContent)

        inputConnection?.setOutput(state)
        inputConnection?.setOutputListener(stateListener)
        applyNewTextWatcher(CardNumberTextWatcher(divider))
        applyInputType()
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
        return primaryRes?.run {
            val c_icon_size_h = resources.getDimension(R.dimen.c_icon_size_h).toInt()
            val c_icon_size_w = resources.getDimension(R.dimen.c_icon_size_w).toInt()
            val drawable = ContextCompat.getDrawable(context, primaryRes)
            drawable?.setBounds(0, 0, c_icon_size_w, c_icon_size_h)
            return drawable
        }
    }

    internal fun setCardPreviewIconGravity(gravity:Int) {
        iconGravity = gravity
    }

    internal fun setCardBrand(c: CustomCardBrand) {
        userFilter.add(c)
        inputConnection?.run()
    }

    private fun applyInputType() {
        val type = inputType
        if(type == InputType.TYPE_TEXT_VARIATION_PASSWORD || type == InputType.TYPE_NUMBER_VARIATION_PASSWORD) {
            inputType = InputType.TYPE_CLASS_DATETIME or InputType.TYPE_TEXT_VARIATION_PASSWORD
        } else {
            inputType = InputType.TYPE_CLASS_DATETIME
        }
        refreshInput()
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
            else -> Logger.i("VGSEditTextView", "Divider for number can't be greater than 1 symbol. (${divider})")
        }
    }

}
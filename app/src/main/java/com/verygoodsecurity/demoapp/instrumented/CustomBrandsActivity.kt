package com.verygoodsecurity.demoapp.instrumented

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.vgscollect.view.card.BrandParams
import com.verygoodsecurity.vgscollect.view.card.CardBrand
import com.verygoodsecurity.vgscollect.view.card.validation.payment.ChecksumAlgorithm
import com.verygoodsecurity.vgscollect.widget.VGSCardNumberEditText
import kotlinx.android.synthetic.main.instrumented_activity_custom_brand.*

class CustomBrandsActivity : AppCompatActivity(), View.OnClickListener {

    var cardNumber: VGSCardNumberEditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.instrumented_activity_custom_brand)

        cardNumber = xmlCardNumber
        cardNumber?.requestFocus()

        createCardNumber?.setOnClickListener(this)
        addBrandBtn?.setOnClickListener(this)
        applyDividerBtn?.setOnClickListener(this)
        inflateCardNumberLay?.setOnClickListener(this)
        attachInflatedCardNumberLay?.setOnClickListener(this)
        overrideExistedBrandBtn?.setOnClickListener(this)
    }

    private fun createCardNumber() {
        cardNumber = VGSCardNumberEditText(this)

        cardNumber?.setCardBrandIconGravity(Gravity.END)

        cardNumber?.id = View.generateViewId()
            .also { activeViewId = it }
    }

    private fun inflateCardNumberLayout() {
        cardNumber = LayoutInflater.from(this).inflate(
            R.layout.instrumented_card_number_layout,
            null,
            false
        ) as VGSCardNumberEditText?

        cardNumber?.setCardBrandIconGravity(Gravity.END)

        cardNumber?.id = View.generateViewId()
            .also { activeViewId = it }
    }

    private fun attachInflatedCardNumberLayout() {
        parentLay?.addView(cardNumber, 0)
        cardNumber?.requestFocus()
    }

    private fun setNewCustomBrand() {
        cardNumber?.addCardBrand(createCardBrand())
    }

    private fun overrideOldBrandByNewCustomBrand() {
        cardNumber?.addCardBrand(createVisaCardBrand())
    }

    private fun applyNewDivider() {
        cardNumber?.setDivider(DIVIDER)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.createCardNumber -> createCardNumber()
            R.id.overrideExistedBrandBtn -> overrideOldBrandByNewCustomBrand()
            R.id.addBrandBtn -> setNewCustomBrand()
            R.id.applyDividerBtn -> applyNewDivider()
            R.id.inflateCardNumberLay -> inflateCardNumberLayout()
            R.id.attachInflatedCardNumberLay -> attachInflatedCardNumberLayout()
        }
    }

    companion object {

        val DIVIDER: Char = '-'

        var activeViewId = -1

        fun createCardBrand(): CardBrand {
            val params = BrandParams(
                "###### ##### ########",
                ChecksumAlgorithm.LUHN,
                arrayOf(15, 19),
                arrayOf(3, 5)
            )
            return CardBrand(
                "^777",
                "newBrand",
                R.drawable.ic_card_back_preview_dark_4,
                params
            )
        }

        fun createVisaCardBrand(): CardBrand {
            val params = BrandParams(
                "###### ##### ########",
                ChecksumAlgorithm.LUHN,
                arrayOf(15, 19),
                arrayOf(3, 5)
            )
            return CardBrand(
                "^41120",
                "newVisa-Brand",
                R.drawable.ic_card_back_preview_dark,
                params
            )
        }
    }
}
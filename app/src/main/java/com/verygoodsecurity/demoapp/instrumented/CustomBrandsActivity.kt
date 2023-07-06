package com.verygoodsecurity.demoapp.instrumented

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.databinding.InstrumentedActivityCustomBrandBinding
import com.verygoodsecurity.vgscollect.view.card.BrandParams
import com.verygoodsecurity.vgscollect.view.card.CardBrand
import com.verygoodsecurity.vgscollect.view.card.validation.payment.ChecksumAlgorithm
import com.verygoodsecurity.vgscollect.widget.VGSCardNumberEditText

class CustomBrandsActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: InstrumentedActivityCustomBrandBinding

    var cardNumber: VGSCardNumberEditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = InstrumentedActivityCustomBrandBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cardNumber = binding.xmlCardNumber
        cardNumber?.requestFocus()

        binding.createCardNumber.setOnClickListener(this)
        binding.addBrandBtn.setOnClickListener(this)
        binding.applyDividerBtn.setOnClickListener(this)
        binding.inflateCardNumberLay.setOnClickListener(this)
        binding.attachInflatedCardNumberLay.setOnClickListener(this)
        binding.overrideExistedBrandBtn.setOnClickListener(this)
    }

    private fun createCardNumber() {
        cardNumber = VGSCardNumberEditText(this)

        cardNumber?.setCardBrandIconGravity(Gravity.END)

        cardNumber?.id = VIEW_ID
    }

    private fun inflateCardNumberLayout() {
        cardNumber = LayoutInflater.from(this).inflate(
            R.layout.instrumented_card_number_layout,
            null,
            false
        ) as VGSCardNumberEditText?

        cardNumber?.setCardBrandIconGravity(Gravity.END)

        cardNumber?.id = VIEW_ID
    }

    private fun attachInflatedCardNumberLayout() {
        binding.parentLay.addView(cardNumber, 0)
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

        val VIEW_ID = View.generateViewId()

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
                com.verygoodsecurity.vgscollect.R.drawable.ic_card_back_preview_dark_4,
                params
            )
        }

        fun createVisaCardBrand(): CardBrand {
            val params = BrandParams(
                "###### ##### ########",
                ChecksumAlgorithm.LUHN,
                arrayOf(16, 19),
                arrayOf(3, 5)
            )
            return CardBrand(
                "^41111",
                "newVisa-Brand",
                com.verygoodsecurity.vgscollect.R.drawable.ic_card_back_preview_dark,
                params
            )
        }
    }
}
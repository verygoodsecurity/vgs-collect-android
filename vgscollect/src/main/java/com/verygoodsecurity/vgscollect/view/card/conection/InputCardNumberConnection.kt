package com.verygoodsecurity.vgscollect.view.card.conection

import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.view.card.ChecksumAlgorithm
import com.verygoodsecurity.vgscollect.view.card.filter.CardBrandPreview
import com.verygoodsecurity.vgscollect.view.card.filter.VGSCardFilter
import com.verygoodsecurity.vgscollect.view.card.validation.MuttableValidator
import com.verygoodsecurity.vgscollect.view.card.validation.VGSValidator
import com.verygoodsecurity.vgscollect.view.card.validation.card.brand.*

/** @suppress */
internal class InputCardNumberConnection(
    private val id:Int,
    private val validator: VGSValidator?,
    private val IcardBrand: IDrawCardBrand? = null,
    private val divider:String? = null
): BaseInputConnection() {
    private val cardFilters = mutableListOf<VGSCardFilter>()


    private var output = VGSFieldState()

    override fun setOutput(state: VGSFieldState) {
        output = state
    }

    override fun getOutput() = output

    override fun setOutputListener(listener: OnVgsViewStateChangeListener?) {
        listener?.let {
            addNewListener(it)
        }
    }

    override fun clearFilters() {
        cardFilters.clear()
    }

    override fun addFilter(filter: VGSCardFilter?) {
        filter?.let {
            cardFilters.add(0, it)
        }
    }

    override fun run() {
        val brand = detectBrand()
        mapValue(brand)

        IcardBrand?.onCardBrandPreview(brand)

        validate(brand)

        notifyAllListeners(id, output)
    }

    private fun validate(brand: CardBrandPreview) {
        val isRequiredRuleValid = isRequiredValid()
        val isContentRuleValid = isContentValid(brand)

        output.isValid = isRequiredRuleValid && isContentRuleValid
    }

    private fun isRequiredValid():Boolean {
        return output.isRequired && !output.content?.data.isNullOrEmpty() || !output.isRequired
    }

    private fun isContentValid(card: CardBrandPreview):Boolean {
        val content = output.content?.data
        return when {
            !output.isRequired && content.isNullOrEmpty() -> true
            output.enableValidation -> checkIsContentValid(card)
            else -> true
        }
    }

    private fun checkIsContentValid(
        card: CardBrandPreview
    ): Boolean {
        val rawStr = output.content?.data?.replace(divider ?: " ", "") ?: ""
        val isStrValid = validator?.isValid(rawStr) ?: false
        val isLuhnValid: Boolean = validateCheckSum(card.algorithm, rawStr)

        val isLengthAppropriate = checkLength(card.numberLength, rawStr.length)
        return isLuhnValid && isStrValid && isLengthAppropriate
    }

    private fun validateCheckSum(
        algorithm: ChecksumAlgorithm,
        cardNumber: String
    ):Boolean {
        return if(algorithm == ChecksumAlgorithm.LUHN) {
            CardBrandDelegate().isValid(cardNumber)
        } else {
            false    // in the future will depends on RULE params
        }
//        val isLuhnValid: Boolean = brandLuhnValidations[card.cardType]?.isValid(rawStr) ?: true
    }

    private fun mapValue(item: CardBrandPreview) {
        applyNewRule(item.regex)

        with(output.content as? FieldContent.CardNumberContent) {
            this?.cardtype = item.cardType
            this?.cardBrandName = item.name
            this?.iconResId = item.resId
            this?.numberRange = item.numberLength
            this?.rangeCVV = item.cvcLength
        }
    }

    private fun applyNewRule(regex: String?) {
        if(validator is MuttableValidator &&
            !regex.isNullOrEmpty()) {
            validator.clearRules()
            validator.addRule(regex)
        }
    }

    private fun detectBrand(): CardBrandPreview {
        for(i in cardFilters.indices) {
            val filter = cardFilters[i]
            val brand = filter.detect(output.content?.data)
            if(brand != null) {
                return brand
            }
        }
        return CardBrandPreview()
    }

    private fun checkLength(
        rangeNumber: Array<Int>,
        length: Int?
    ): Boolean {
        return rangeNumber.contains(length)
    }

    internal interface IDrawCardBrand {
        fun onCardBrandPreview(
            card: CardBrandPreview
        )
    }
}
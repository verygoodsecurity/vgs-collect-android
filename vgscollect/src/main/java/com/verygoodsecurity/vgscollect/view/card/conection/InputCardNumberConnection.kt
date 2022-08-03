package com.verygoodsecurity.vgscollect.view.card.conection

import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.view.card.filter.CardBrandPreview
import com.verygoodsecurity.vgscollect.view.card.filter.VGSCardFilter
import com.verygoodsecurity.vgscollect.view.card.validation.CompositeValidator
import com.verygoodsecurity.vgscollect.view.card.validation.payment.ChecksumAlgorithm
import com.verygoodsecurity.vgscollect.view.card.validation.payment.brand.LuhnCheckSumValidator

/** @suppress */
internal class InputCardNumberConnection(
    id: Int,
    validator: CompositeValidator,
    private val IcardBrand: IDrawCardBrand? = null,
    private val divider: String? = null
) : BaseInputConnection(id, validator) {

    var canOverrideDefaultValidation = false

    private val cardFilters = mutableListOf<VGSCardFilter>()

    override fun setOutputListener(listener: OnVgsViewStateChangeListener?) {
        listener?.let {
            addNewListener(it)
        } ?: clearAllListeners()
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

        notifyAllListeners(state)
    }

    private fun validate(brand: CardBrandPreview) {
        val isRequiredRuleValid = isRequiredValid()
        val isContentRuleValid = isContentValid(brand)

        state.isValid = isRequiredRuleValid && isContentRuleValid
    }

    private fun isRequiredValid(): Boolean {
        return state.isRequired && !state.content?.data.isNullOrEmpty() || !state.isRequired
    }

    private fun isContentValid(card: CardBrandPreview): Boolean {
        val content = state.content?.data
        return when {
            !state.isRequired && content.isNullOrEmpty() -> true
            state.enableValidation -> checkIsContentValid(card)
            else -> true
        }
    }

    private fun checkIsContentValid(card: CardBrandPreview): Boolean {
        val rawStr = state.content?.data?.replace(divider ?: " ", "") ?: ""

        return if (canOverrideDefaultValidation || !card.successfullyDetected) {
            isValid(rawStr)
        } else {
            val isLengthAppropriate: Boolean = checkLength(card.numberLength, rawStr.length)
            val isLuhnValid: Boolean = validateCheckSum(card.algorithm, rawStr)
            isLengthAppropriate && isLuhnValid
        }
    }

    private fun validateCheckSum(
        algorithm: ChecksumAlgorithm,
        cardNumber: String
    ): Boolean {
        return when (algorithm) {
            ChecksumAlgorithm.LUHN -> LuhnCheckSumValidator().isValid(cardNumber)
            ChecksumAlgorithm.NONE -> true
            else -> false
        }
    }

    private fun mapValue(item: CardBrandPreview) {
        with(state.content as? FieldContent.CardNumberContent) {
            this?.cardtype = item.cardType
            this?.cardBrandName = item.name
            this?.iconResId = item.resId
            this?.numberRange = item.numberLength
            this?.rangeCVV = item.cvcLength
        }
    }

    private fun detectBrand(): CardBrandPreview {
        for (i in cardFilters.indices) {
            val filter = cardFilters[i]
            return filter.detect(state.content?.data)
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
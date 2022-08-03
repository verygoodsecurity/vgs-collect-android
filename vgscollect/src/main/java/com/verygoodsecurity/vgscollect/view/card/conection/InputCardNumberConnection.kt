package com.verygoodsecurity.vgscollect.view.card.conection

import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.view.card.filter.CardBrandPreview
import com.verygoodsecurity.vgscollect.view.card.filter.CardInputFilter
import com.verygoodsecurity.vgscollect.view.card.filter.VGSCardFilter
import com.verygoodsecurity.vgscollect.view.card.validation.CheckSumValidator
import com.verygoodsecurity.vgscollect.view.card.validation.CompositeValidator
import com.verygoodsecurity.vgscollect.view.card.validation.LengthMatchValidator

/** @suppress */
internal class InputCardNumberConnection(
    id: Int,
    validator: CompositeValidator,
    private val IcardBrand: IDrawCardBrand? = null,
    private val divider: String? = null
) : BaseInputConnection(id, validator), CardInputFilter {

    var canOverrideDefaultValidation = false

    private val cardFilters = mutableListOf<VGSCardFilter>()

    override fun getRawContent(content: String?): String {
        return state.content?.data?.replace(divider ?: " ", "") ?: ""
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

        val errors = validate(brand)
        state.isValid = errors.isEmpty()
        state.errors = errors
        notifyAllListeners(state)
    }

    private fun validate(card: CardBrandPreview): List<String> {
        val content = state.content?.data
        return when {
            !state.isRequired && content.isNullOrEmpty() -> emptyList()
            state.enableValidation -> checkIsContentValid(card)
            else -> emptyList()
        }
    }

    private fun checkIsContentValid(card: CardBrandPreview): List<String> {
        val rawStr = getRawContent(state.content?.data)
        return if (canOverrideDefaultValidation || !card.successfullyDetected) {
            validator.isValid(rawStr)
        } else {
            listOfNotNull(
                LengthMatchValidator(card.numberLength).isValid(rawStr),
                CheckSumValidator(card.algorithm).isValid(rawStr)
            )
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

    internal interface IDrawCardBrand {

        fun onCardBrandPreview(card: CardBrandPreview)
    }
}
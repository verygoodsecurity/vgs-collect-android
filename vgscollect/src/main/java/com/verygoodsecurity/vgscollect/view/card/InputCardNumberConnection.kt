package com.verygoodsecurity.vgscollect.view.card

import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.view.card.filter.CardBrandWrapper
import com.verygoodsecurity.vgscollect.view.card.filter.VGSCardFilter
import com.verygoodsecurity.vgscollect.view.card.validation.MuttableValidator
import com.verygoodsecurity.vgscollect.view.card.validation.VGSValidator
import com.verygoodsecurity.vgscollect.view.card.validation.card.brand.*

/** @suppress */
internal class InputCardNumberConnection(
    private val id:Int,
    private val validator: VGSValidator?,
    private val IcardBrand: IdrawCardBrand? = null,
    private val divider:String? = null
): BaseInputConnection() {
    private val cardFilters = mutableListOf<VGSCardFilter>()
    private val brandLuhnValidations by lazy {
        val set = HashMap<CardType, VGSValidator>()
        set[CardType.VISA] = VisaDelegate()
        set[CardType.VISA_ELECTRON] = VisaElectronDelegate()
        set[CardType.MASTERCARD] = MastercardDelegate()
        set[CardType.AMERICAN_EXPRESS] = AmexDelegate()
        set[CardType.DINCLUB] = DinersClubDelegate()
        set[CardType.DISCOVER] = DiscoverDelegate()
        set[CardType.JCB] = JcbDelegate()

        set
    }


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
        val card = runFilters()
        mapValue(card)

        IcardBrand?.drawCardBrandPreview()

        applyNewRule(card.regex)

        output.isValid = isRequiredValid() && isContentValid(card)

        notifyAllListeners(id, output)
    }

    private fun isRequiredValid():Boolean {
        return output.isRequired && !output.content?.data.isNullOrEmpty() || !output.isRequired
    }

    private fun isContentValid(card: CardBrandWrapper):Boolean {
        val content = output.content?.data
        return when {
            !output.isRequired && content.isNullOrEmpty() -> true
            output.enableValidation -> checkIsContentValid(card)
            else -> true
        }
    }

    private fun checkIsContentValid(
        card: CardBrandWrapper
    ): Boolean {
        val rawStr = output.content?.data?.replace(divider ?: " ", "") ?: ""
        val isStrValid = validator?.isValid(rawStr) ?: false
        val isLuhnValid: Boolean = brandLuhnValidations[card.cardType]?.isValid(rawStr) ?: true

        val isLengthAppropriate = checkLength(card.cardType, rawStr.length)
        return isLuhnValid && isStrValid && isLengthAppropriate
    }

    private fun mapValue(item: CardBrandWrapper) {
        val card = (output.content as? FieldContent.CardNumberContent)
        card?.cardtype = item.cardType
        card?.cardBrandName = item.name
        card?.iconResId = item.resId
    }

    private fun applyNewRule(regex: String?) {
        if(validator is MuttableValidator &&
            !regex.isNullOrEmpty()) {
                validator.clearRules()
                validator.addRule(regex)
        }
    }

    private fun runFilters(): CardBrandWrapper {
        for(i in cardFilters.indices) {
            val filter = cardFilters[i]
            val brand = filter.detect(output.content?.data)
            if(brand != null) {
                return brand
            }
        }
        return CardBrandWrapper()
    }

    private fun checkLength(
        cardtype: CardType,
        length: Int?
    ): Boolean {
        return cardtype.rangeNumber.contains(length)
    }

    interface IdrawCardBrand {
        fun drawCardBrandPreview()
    }
}
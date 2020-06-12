package com.verygoodsecurity.vgscollect.view.card.formatter

import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.view.card.CardType

/**
 * You can use this class to create customize card numbers mask inside the [VGSCardNumberEditText].
 */
open class CardMaskAdapter {

    /** @suppress */
    internal fun getItem(
        cardType: CardType,
        name: String,
        bin: String,
        mask: String
    ): String {
        return getMask(cardType, name, bin, mask)
    }

    /**
     * Returns prepared mask to display in [VGSCardNumberEditText]
     * This method trigger when field detect new cardBrand.
     *
     * @param cardType detected card brand type
     * @param name detected card brand name
     * @param bin detected card brand bin
     * @param mask default format of the current card number
     *
     * @return String mask for the card number.
     */
    protected open fun getMask(
        cardType: CardType,
        name: String,
        bin: String,
        mask: String
    ): String {
        return mask
    }
}
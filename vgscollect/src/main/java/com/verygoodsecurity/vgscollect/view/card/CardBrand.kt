package com.verygoodsecurity.vgscollect.view.card

/**
 * The data class definition for represent custom card brand.
 * It may be useful to add new brands in addition to already defined brands or override existing ones.
 *
 * @constructor
 * @param regex - The regex rules for detection card brand.
 * @param cardBrandName - The name of current card brand. This name may be visible for users.
 * @param drawableResId - The drawable resource represents credit card logo.
 * @param params - The set of parameters for card brand creation.
 */
data class CardBrand(
    val regex: String,
    val cardBrandName: String,
    val drawableResId: Int = 0,
    val params: BrandParams = BrandParams()
) {

    internal var cardType: CardType = CardType.UNKNOWN

    internal constructor(
        cardType: CardType,
        regex: String,
        cardBrandName: String,
        drawableResId: Int,
        params: BrandParams
    ) : this(regex, cardBrandName, drawableResId, params) {
        this.cardType = cardType
    }
}
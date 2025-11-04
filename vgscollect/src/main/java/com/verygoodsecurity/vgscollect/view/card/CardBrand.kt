package com.verygoodsecurity.vgscollect.view.card

/**
 * Represents a card brand.
 *
 * @param regex The regex used to identify the card brand.
 * @param cardBrandName The name of the card brand.
 * @param drawableResId The drawable resource ID for the card brand's icon.
 * @param params The parameters for the card brand.
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
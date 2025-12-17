package com.verygoodsecurity.vgscollect.view.cvc

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.view.card.CardType

/**
 * An adapter for creating custom CVC icons.
 */
open class CVCIconAdapter(private val context: Context) {

    private val defaultIcon: Drawable by lazy {
        AppCompatResources.getDrawable(context, R.drawable.ic_card_back_preview_dark)!!
    }

    private val defaultIconWidth = context.resources.getDimension(R.dimen.c_icon_size_w).toInt()
    private val defaultIconHeight = context.resources.getDimension(R.dimen.c_icon_size_h).toInt()

    /**
     * Returns a drawable object associated with a particular resource ID.
     *
     * @param resId The resource ID of the drawable.
     *
     * @return The drawable object, or the default drawable if the resource is not found.
     */
    protected fun getDrawable(@DrawableRes resId: Int) =
        AppCompatResources.getDrawable(context, resId) ?: defaultIcon

    /**
     * Returns the icon to be displayed in the CVC field.
     *
     * @param cardType The detected card type.
     * @param cardBrand The name of the card brand.
     * @param cvcLength The length of the CVC.
     * @param r The bounds of the input field.
     *
     * @return The drawable to be displayed.
     */
    protected open fun getIcon(
        cardType: CardType,
        cardBrand: String?,
        cvcLength: Int,
        r: Rect
    ): Drawable {
        val drawable = getDrawable(getDrawableId(cardType))
        drawable.bounds = this@CVCIconAdapter.getDefaultBounds()
        return drawable
    }

    /**
     * Returns the bounds for the CVC icon.
     *
     * @return The bounds for the drawable.
     */
    protected open fun getDefaultBounds(): Rect = Rect(0, 0, defaultIconWidth, defaultIconHeight)

    /** @suppress */
    internal fun getItem(
        cardType: CardType,
        cardBrand: String?,
        cvcLength: Int,
        r: Rect
    ): Drawable {
        val icon = getIcon(cardType, cardBrand, cvcLength, r)
        if (icon.bounds.isEmpty) {
            icon.bounds = this.getDefaultBounds()
        }
        return icon
    }

    @DrawableRes
    /** @suppress */
    private fun getDrawableId(cardType: CardType): Int = when (cardType) {
        CardType.AMERICAN_EXPRESS -> R.drawable.ic_card_back_preview_dark_4
        else -> R.drawable.ic_card_back_preview_dark
    }
}
package com.verygoodsecurity.vgscollect.view.cvc

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.view.card.CardType

/**
 * You can use this class to create custom Drawables as a preview image for the [CardVerificationCodeEditText].
 */
open class CVCIconAdapter(private val context: Context) {

    private val defaultIcon: Drawable by lazy {
        AppCompatResources.getDrawable(context, R.drawable.ic_card_back_preview_dark)!!
    }

    private val defaultIconWidth = context.resources.getDimension(R.dimen.c_icon_size_w).toInt()
    private val defaultIconHeight = context.resources.getDimension(R.dimen.c_icon_size_h).toInt()

    /**
     * Returns a drawable object associated with a particular resource ID or default drawable.
     */
    protected fun getDrawable(@DrawableRes resId: Int) =
        AppCompatResources.getDrawable(context, resId) ?: defaultIcon

    /**
     * Returns prepared Drawable to display in [CardVerificationCodeEditText]
     *
     * @param cardType detected card brand type
     * @param cardBrand card brand name
     * @param cvcLength cvc length
     * @param r size of the input field. The size will be 0 if View is not visible.
     *
     * @return Drawable object for the preview.
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
     * Return the Rect object for the drawable's bounds. You may change the object returned by this
     * method to setup dimensions of the preview.
     *
     * The default preview image has 32dp for the width and 21dp for the height.
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
package com.verygoodsecurity.vgscollect.view.card.icon

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.view.card.CardType

/**
 * You can use this class to create custom Drawables as a preview image for the [com.verygoodsecurity.vgscollect.widget.VGSCardNumberEditText].
 */
open class CardIconAdapter(private val context: Context) {

    private val defaultIcon: Drawable by lazy {
        AppCompatResources.getDrawable(context, R.drawable.ic_card_back_preview_dark)!!
    }

    private val defaultWidth: Int = context.resources.getDimension(R.dimen.c_icon_size_w).toInt()
    private val defaultHeight: Int = context.resources.getDimension(R.dimen.c_icon_size_h).toInt()

    /**
     * Returns a drawable object associated with a particular resource ID.
     */
    protected fun getDrawable(resId: Int): Drawable =
        AppCompatResources.getDrawable(context, resId) ?: defaultIcon

    /**
     * Return the Rect object for the drawable's bounds. You may change the object returned by this
     * method to setup dimensions of the preview.
     *
     * The default preview image has 32dp for the width and 21dp for the height.
     *
     * @return The bounds for the drawable.
     */
    protected open fun getDefaultBounds(): Rect = Rect(0, 0, defaultWidth, defaultHeight)

    /**
     * Returns prepared Drawable to display in [com.verygoodsecurity.vgscollect.widget.VGSCardNumberEditText]
     * This method trigger when field detect new cardBrand.
     *
     * @param cardType detected card brand type
     * @param name detected card brand name
     * @param resId default resource id for detected card brand
     * @param r size of the input field. The size will be 0 if View is not visible.
     *
     * @return Drawable object for the preview.
     */
    protected open fun getIcon(
        cardType: CardType,
        name: String?,
        resId: Int,
        r: Rect
    ): Drawable {
        val drawable = getDrawable(resId)
        drawable.bounds = getDefaultBounds()
        return drawable
    }

    /** @suppress */
    internal fun getItem(
        cardType: CardType,
        name: String?,
        resId: Int,
        r: Rect
    ): Drawable {
        val icon = getIcon(cardType, name, resId, r)
        if (icon.bounds.isEmpty) {
            icon.bounds = getDefaultBounds()
        }
        return icon
    }
}
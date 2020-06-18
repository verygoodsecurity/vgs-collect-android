package com.verygoodsecurity.vgscollect.view.card.icon

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.view.card.CardType

/**
 * You can use this class to create custom Drawables as a preview image for the [VGSCardNumberEditText].
 */
open class CardIconAdapter(
    private val context: Context
) {

    /**
     * Returns a drawable object associated with a particular resource ID.
     */
    protected fun getDrawable(resId: Int): Drawable {
        return AppCompatResources.getDrawable(context, resId) ?: AppCompatResources.getDrawable(context, R.drawable.ic_card_back_preview_dark)!!
    }

    /**
     * Return the Rect object for the drawable's bounds. You may change the object returned by this
     * method to setup dimensions of the preview.
     *
     * The default preview image has 32dp for the width and 21dp for the height.
     *
     * @return The bounds for the drawable.
     */
    private fun getBounds(): Rect {
        val c_icon_size_w = context.resources.getDimension(R.dimen.c_icon_size_w).toInt()
        val c_icon_size_h = context.resources.getDimension(R.dimen.c_icon_size_h).toInt()

        return Rect(0, 0, c_icon_size_w, c_icon_size_h)
    }

    /** @suppress */
    internal fun getItem(
        cardType: CardType,
        name: String?,
        resId: Int,
        r:Rect
    ):Drawable {

        val icon = getIcon(cardType, name, resId, r)
        if(icon.bounds.isEmpty) {
            icon.bounds = getBounds()
        }

        return icon
    }

    /**
     * Returns prepared Drawable to display in [VGSCardNumberEditText]
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
        r:Rect
    ):Drawable {
        val drawable = getDrawable(resId)

        drawable.bounds = getBounds()

        return drawable
    }
}
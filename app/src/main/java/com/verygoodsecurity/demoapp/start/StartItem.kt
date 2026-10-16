package com.verygoodsecurity.demoapp.start

import android.app.Activity
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.verygoodsecurity.demoapp.R

/** Status label shown next to a flow title; [RECOMMENDED] also raises the card. */
enum class StartBadge(@StringRes val label: Int) {
    DEPRECATED(R.string.start_badge_deprecated),
    RECOMMENDED(R.string.start_badge_recommended)
}

sealed interface StartItem {

    val id: Int

    data class Header(@StringRes val title: Int) : StartItem {

        override val id: Int get() = title
    }

    data class Flow(
        @StringRes val title: Int,
        @StringRes val description: Int,
        @DrawableRes val icon: Int,
        val screen: Class<out Activity>,
        val routeId: String? = null,
        val badge: StartBadge? = null,
        /** `true` when [icon] is a white glyph that needs the gradient tile; `false` when it is already a full tile. */
        val glyph: Boolean = false
    ) : StartItem {

        override val id: Int get() = title
    }
}

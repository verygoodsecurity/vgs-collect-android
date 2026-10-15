package com.verygoodsecurity.demoapp.start

import android.app.Activity
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

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
        val routeId: String? = null
    ) : StartItem {

        override val id: Int get() = title
    }
}

package com.verygoodsecurity.vgscollect.widget.core

/**
 * A listener for receiving notifications about visibility changes.
 */
interface VisibilityChangeListener {
    /**
     * Called when the view becomes visible.
     */
    fun onShow()

    /**
     * Called when the view becomes hidden.
     */
    fun onDismiss()
}
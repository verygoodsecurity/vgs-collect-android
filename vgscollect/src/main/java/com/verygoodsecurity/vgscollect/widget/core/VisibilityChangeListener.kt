package com.verygoodsecurity.vgscollect.widget.core

/**
 * Interface definition for a callback to be invoked when the DatePicker Dialog changes
 * visibility.
 */
interface VisibilityChangeListener {
    /**
     * Called when the DatePicker Dialog was shown.
     */
    fun onShow()

    /**
     * Called when the DatePicker Dialog was dismissed.
     */
    fun onDismiss()
}
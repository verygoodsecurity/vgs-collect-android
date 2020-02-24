package com.verygoodsecurity.vgscollect.view

import android.view.View
import com.verygoodsecurity.vgscollect.core.storage.DependencyListener

internal interface AccessibilityStatePreparer {
    fun getView(): View
    fun getDependencyListener(): DependencyListener
}
package com.verygoodsecurity.vgscollect.view.card

import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState

interface InputRunnable:Runnable {
    fun setOutput(state: VGSFieldState)
    fun getOutput(): VGSFieldState

    fun setOnVgsViewStateChangeListener(l: OnVgsViewStateChangeListener?)
}
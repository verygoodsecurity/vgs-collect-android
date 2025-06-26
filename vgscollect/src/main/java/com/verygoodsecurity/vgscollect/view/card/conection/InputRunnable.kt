package com.verygoodsecurity.vgscollect.view.card.conection

import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState

/** @suppress */
interface InputRunnable : Runnable {

    fun getOutput(): VGSFieldState

    fun setOutput(state: VGSFieldState)

    fun addOutputListener(listener: OnVgsViewStateChangeListener?)

    fun removeOutputListener(listener: OnVgsViewStateChangeListener?)
}
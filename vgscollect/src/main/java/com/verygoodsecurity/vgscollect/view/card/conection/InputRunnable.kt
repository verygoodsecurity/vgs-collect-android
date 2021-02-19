package com.verygoodsecurity.vgscollect.view.card.conection

import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.view.card.filter.CardInputFilter

/** @suppress */
interface InputRunnable:Runnable, CardInputFilter {
    fun setOutput(state: VGSFieldState)
    fun getOutput(): VGSFieldState

    fun setOutputListener(listener: OnVgsViewStateChangeListener?)
}
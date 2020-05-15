package com.verygoodsecurity.vgscollect.view.card

import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState

internal abstract class BaseInputConnection: InputRunnable {
    private var stateListeners = mutableListOf<OnVgsViewStateChangeListener>()

    protected fun addNewListener(listener: OnVgsViewStateChangeListener) {
        if(!stateListeners.contains(listener)) {
            stateListeners.add(listener)
            run()
        }
    }

    protected fun notifyAllListeners(
        id: Int,
        output: VGSFieldState
    ) {
        stateListeners.forEach { stateListener->
            stateListener.emit(id, output)
        }
    }
}
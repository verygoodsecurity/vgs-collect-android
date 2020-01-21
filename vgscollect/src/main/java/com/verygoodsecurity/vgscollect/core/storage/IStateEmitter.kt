package com.verygoodsecurity.vgscollect.core.storage

import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener

interface IStateEmitter {
    fun performSubscription(): OnVgsViewStateChangeListener
    fun attachStateChangeListener(listener:OnFieldStateChangeListener?)
    fun attachFieldDependencyObserver(listener:FieldDependencyObserver?)
}
package com.verygoodsecurity.vgscollect.core.model.state

sealed class FieldState {
    var hasFocus:Boolean = false
        internal set
    var isValid:Boolean = false
        internal set
    var isEmpty:Boolean = false
        internal set
    var isRequired:Boolean = false
        internal set
    var alias:String = ""
        internal set

    class CardNumberState(val bin:String?,
                          val last4:String?
    ):FieldState()
    object CVVState:FieldState()
    object CardName:FieldState()
    object CardExpirationDate:FieldState()
}

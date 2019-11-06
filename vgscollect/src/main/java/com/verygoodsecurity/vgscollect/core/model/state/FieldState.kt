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

    class CardNumberState(
        content: String?,
        type: String
    ):FieldState() {
        val bin:String?
        val last4:String?
        val cardType:String = type

        init {
            bin = when {
                content.isNullOrEmpty() -> ""
                content.length > 7 -> content.substring(0, 7)
                else -> content.substring(0, content.length)
            }
            last4 = when {
                content.isNullOrEmpty() -> ""
                content.length > 14 -> content.substring(14, content.length)
                else -> ""
            }
        }
    }
    object CVVState:FieldState()
    object CardName:FieldState()
    object CardExpirationDate:FieldState()
}

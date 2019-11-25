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
    var fieldName:String = ""
        internal set

    class CardNumberState(
        content: String?,
        type: String
    ):FieldState() {
        val bin:String? = content?.replace(" ".toRegex(), "")?.run {
                if(length > 7) {
                    substring(0, 6)
                } else {
                    substring(0, length)
                }
            }

        val last4:String? = content?.replace(" ".toRegex(), "")?.run {
                if(length > 12) {
                    substring(12, length)
                } else {
                    ""
                }
            }

        val cardType:String = type
    }

    object CVCState:FieldState()
    object CardName:FieldState()
    object CardExpirationDate:FieldState()
}

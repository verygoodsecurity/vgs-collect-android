package com.verygoodsecurity.vgscollect.core.model.state

import com.verygoodsecurity.vgscollect.view.card.FieldType

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
    var fieldType: FieldType = FieldType.INFO
    internal set

    class CardNumberState:FieldState() {
        var bin:String? = ""
            internal set
        var last:String? = ""
            internal set
        var number:String? = ""
            internal set
        var cardBrand: String? = ""
            internal set
        var resId: Int = 0
            internal set
    }

    class CVCState:FieldState()
    class CardName:FieldState()
    class CardExpirationDate:FieldState()
    class Info:FieldState()
}

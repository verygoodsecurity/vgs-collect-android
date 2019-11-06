package com.verygoodsecurity.vgscollect.core.model

import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.view.text.validation.card.VGSTextInputType

data class VGSFieldState(var isFocusable:Boolean = false,
                         var isRequired:Boolean = true,
                         var type: VGSTextInputType = VGSTextInputType.CardOwnerName,
                         var content:String? = null,
                         var alias:String? = null) {  /// Field name - actualy this is key for you JSON wich contains data

    fun isValid():Boolean {
        val str = content?.replace(" ", "")
        return type.validate(str)
    }
}

fun VGSFieldState.mapToFieldState():FieldState {
    val f = when(type) {
        is VGSTextInputType.CardNumber -> {
            FieldState.CardNumberState(content, (type as VGSTextInputType.CardNumber).card.name)
        }
        is VGSTextInputType.CardOwnerName -> FieldState.CardName
        is VGSTextInputType.CVVCardCode -> FieldState.CVVState
        is VGSTextInputType.CardExpDate -> FieldState.CardExpirationDate
    }
    f.isValid = type.validate(content)

    f.isEmpty = content.isNullOrEmpty()
    f.isRequired = isRequired
    f.alias = alias?:""
    f.hasFocus = isFocusable
    return f
}

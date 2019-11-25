package com.verygoodsecurity.vgscollect.core.model.state

import com.verygoodsecurity.vgscollect.view.text.validation.card.VGSTextInputType

data class VGSFieldState(var isFocusable:Boolean = false,
                         var isRequired:Boolean = true,
                         var type: VGSTextInputType = VGSTextInputType.CardOwnerName,
                         var content:String? = null,
                         var fieldName:String? = null) {  /// Field name - actually this is key for you JSON which contains data

    fun isValid():Boolean {
        return if(isRequired) {
            type.validate(content)
        } else {
            content.isNullOrEmpty() || type.validate(content)
        }
    }
}

fun VGSTextInputType.mapVGSTextInputTypeToFieldState(content: String? = null):FieldState {
    return when(this) {
        is VGSTextInputType.CardNumber -> {
            FieldState.CardNumberState(content, this.card.name)
        }
        is VGSTextInputType.CardOwnerName -> FieldState.CardName
        is VGSTextInputType.CVCCardCode -> FieldState.CVCState
        is VGSTextInputType.CardExpDate -> FieldState.CardExpirationDate
    }
}

fun VGSFieldState.mapToFieldState():FieldState {
    val f = type.mapVGSTextInputTypeToFieldState(content)

    f.isValid = type.validate(content)

    f.isEmpty = content.isNullOrEmpty()
    f.isRequired = isRequired
    f.fieldName = fieldName?:""
    f.hasFocus = isFocusable
    return f
}

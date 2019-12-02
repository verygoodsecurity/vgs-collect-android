package com.verygoodsecurity.vgscollect.core.model.state

import com.verygoodsecurity.vgscollect.view.text.validation.card.VGSEditTextFieldType

data class VGSFieldState(var isFocusable:Boolean = false,
                         var isRequired:Boolean = true,
                         var type: VGSEditTextFieldType = VGSEditTextFieldType.CardHolderName,
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

fun VGSEditTextFieldType.mapVGSTextInputTypeToFieldState(content: String? = null):FieldState {
    return when(this) {
        is VGSEditTextFieldType.CardNumber -> {
            FieldState.CardNumberState(content, this.card.name)
        }
        is VGSEditTextFieldType.CardHolderName -> FieldState.CardName
        is VGSEditTextFieldType.CVCCardCode -> FieldState.CVCState
        is VGSEditTextFieldType.CardExpDate -> FieldState.CardExpirationDate
        is VGSEditTextFieldType.Info -> FieldState.Info
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

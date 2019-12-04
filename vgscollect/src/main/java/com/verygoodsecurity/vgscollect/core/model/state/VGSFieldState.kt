package com.verygoodsecurity.vgscollect.core.model.state

import com.verygoodsecurity.vgscollect.view.text.validation.card.FieldType

data class VGSFieldState(var isFocusable:Boolean = false,
                         var isRequired:Boolean = true,
                         var isValid:Boolean = true,
                         var type: FieldType = FieldType.INFO,
                         var content:FieldContent? = null,
                         var fieldName:String? = null) {  /// Field name - actually this is key for you JSON which contains data
}

fun VGSFieldState.mapToFieldState():FieldState {
    val f = when(type) {
        FieldType.INFO -> FieldState.Info
        FieldType.CVC -> FieldState.CardName
        FieldType.CARD_HOLDER_NAME -> FieldState.CardName
        FieldType.CARD_EXPIRATION_DATE -> FieldState.CardName
        FieldType.CARD_NUMBER -> {
            val c = FieldState.CardNumberState
            c.bin = (content as? FieldContent.CardNumberContent)?.parseCardBin()
            c.last4 = (content as? FieldContent.CardNumberContent)?.parseCardLast4()
            c.number = (content as? FieldContent.CardNumberContent)?.parseCardNumber()
            c.cardBrand = (content as? FieldContent.CardNumberContent)?.cardtype?.name
            c.resId = (content as? FieldContent.CardNumberContent)?.cardtype?.resId?:0
            c
        }
    }

    f.fieldType = type
    f.isValid = isValid
    f.isEmpty = content?.data.isNullOrEmpty()
    f.isRequired = isRequired
    f.fieldName = fieldName?:""
    f.hasFocus = isFocusable
    return f
}

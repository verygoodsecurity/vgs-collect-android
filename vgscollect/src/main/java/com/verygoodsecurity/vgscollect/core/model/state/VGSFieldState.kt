package com.verygoodsecurity.vgscollect.core.model.state

import com.verygoodsecurity.vgscollect.view.card.FieldType

/** @suppress */
data class VGSFieldState(var isFocusable:Boolean = false,
                         var isRequired:Boolean = true,
                         var isValid:Boolean = true,
                         var type: FieldType = FieldType.INFO,
                         var content:FieldContent? = null,
                         var fieldName:String? = null) {

    override fun toString(): String {
        return "isFocusable: $isFocusable\n"+
                "isRequired: $isRequired\n" +
                "isValid: $isValid\n" +
                "type: $type\n" +
                "content: ${content.toString()}\n" +
                "fieldName: $fieldName\n"
    }
}

/** @suppress */
internal fun VGSFieldState.mapToFieldState():FieldState {
    val f = when(type) {
        FieldType.INFO -> FieldState.InfoState()
        FieldType.CVC -> FieldState.CVCState()
        FieldType.CARD_HOLDER_NAME -> FieldState.CardHolderNameState()
        FieldType.CARD_EXPIRATION_DATE -> FieldState.CardExpirationDateState()
        FieldType.CARD_NUMBER -> {
            val state = FieldState.CardNumberState()
            
            val content = (content as? FieldContent.CardNumberContent)
            if(isValid) {
                state.bin = content?.parseCardBin()
                state.last = content?.parseRawCardLastDigits()
            }
            state.number = content?.parseCardNumber()
            state.cardBrand = content?.cardBrandName?:""
            state.drawableBrandResId = content?.iconResId?:0

            state
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

/** @suppress */
internal fun VGSFieldState.isCardNumberType() = type == FieldType.CARD_NUMBER

/** @suppress */
internal fun VGSFieldState.isCardCVCType() = type == FieldType.CVC

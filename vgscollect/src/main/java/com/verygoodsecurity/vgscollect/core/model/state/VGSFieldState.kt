package com.verygoodsecurity.vgscollect.core.model.state

import com.verygoodsecurity.vgscollect.view.card.FieldType

/** @suppress */
data class VGSFieldState(
    var isFocusable: Boolean = false,
    var isRequired: Boolean = true,
    var enableValidation: Boolean = true,
    var isValid: Boolean = true,
    var type: FieldType = FieldType.INFO,
    var content: FieldContent? = null,
    var fieldName: String? = null,
    var hasUserInteraction: Boolean = false
) {

    override fun toString(): String {
        return "isFocusable: $isFocusable\n" +
                "isRequired: $isRequired\n" +
                "isValid: $isValid\n" +
                "type: $type\n" +
                "content: ${content.toString()}\n" +
                "fieldName: $fieldName\n"
    }
}

/** @suppress */
internal fun VGSFieldState.mapToFieldState(): FieldState {
    val f = when (type) {
        FieldType.INFO -> FieldState.InfoState()
        FieldType.CVC -> FieldState.CVCState()
        FieldType.CARD_HOLDER_NAME -> FieldState.CardHolderNameState()
        FieldType.CARD_EXPIRATION_DATE -> FieldState.CardExpirationDateState()
        FieldType.CARD_NUMBER -> {
            val state = FieldState.CardNumberState()

            val content = (content as? FieldContent.CardNumberContent)
            if (isValid) {
                state.bin = content?.parseCardBin()
                state.last = content?.parseCardLast4Digits()
            }
            state.contentLengthRaw = content?.rawData?.length ?: 0
            state.number = content?.parseCardNumber()
            state.cardBrand = content?.cardBrandName ?: ""
            state.drawableBrandResId = content?.iconResId ?: 0

            state
        }
        FieldType.SSN -> {
            val state = FieldState.SSNNumberState()
            val content = (content as? FieldContent.SSNContent)
            if (isValid) {
                state.last = content?.parseCardLast4Digits()
            }
            state.contentLengthRaw = content?.rawData?.length ?: 0

            state
        }
    }

    f.fieldType = type
    f.isValid = isValid

    f.contentLength = content?.data?.length ?: 0
    f.isEmpty = f.contentLength == 0

    f.isRequired = isRequired
    f.fieldName = fieldName ?: ""
    f.hasFocus = isFocusable
    return f
}

/** @suppress */
internal fun VGSFieldState.isCardNumberType() = type == FieldType.CARD_NUMBER

/** @suppress */
internal fun VGSFieldState.isCardCVCType() = type == FieldType.CVC

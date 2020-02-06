package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import com.verygoodsecurity.vgscollect.view.card.FieldType

internal object InputFieldFabric {

    fun getInputField(context: Context, type:FieldType):BaseInputField {
        return when(type) {
            FieldType.CARD_NUMBER -> CardInputField(context)
            FieldType.CVC -> CVCInputField(context)
            FieldType.CARD_EXPIRATION_DATE -> DateInputField(context)
            FieldType.CARD_HOLDER_NAME -> PersonNameInputField(context)
            FieldType.INFO -> InfoInputField(context)
        }
    }

}
package com.verygoodsecurity.vgscollect.core.model

import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.view.text.validation.card.VGSTextInputType
import java.util.regex.Pattern

data class VGSFieldState(var isFocusable:Boolean = false,
                         var isRequired:Boolean = true,
                         var type: VGSTextInputType = VGSTextInputType.CardOwnerName,
                         var placeholder:String? = null,
                         var content:String? = null,
                         var alias:String? = null)  /// Field name - actualy this is key for you JSON wich contains data

fun VGSFieldState.isValid():Boolean {
    val p = Pattern.compile(type.validation)
    return p.matcher(content).matches()
}

fun VGSFieldState.mapToFieldState():FieldState {
    val f = when(type) {
        is VGSTextInputType.CardNumber -> {
            if(content != null) {
                val bin = content?.run {
                    val lastBinPosition = if(length > 7) 7 else length
                    if(length > 14) {
                        substring(0, lastBinPosition)
                    } else {
                        ""
                    }
                }
                val last4  = content?.run {
                    if(length > 14) {
                        substring(14, this.length)
                    } else {
                        ""
                    }
                }?:""
                FieldState.CardNumberState(bin, last4)
            } else {
                FieldState.CardNumberState("", "")
            }
        }
        is VGSTextInputType.CardOwnerName -> FieldState.CardName
        is VGSTextInputType.CVVCardCode -> FieldState.CVVState
        is VGSTextInputType.CardExpDate -> FieldState.CardExpirationDate
    }
    f.isEmpty = content.isNullOrEmpty()
    f.isRequired = isRequired
    f.alias = alias?:""
    f.hasFocus = isFocusable
    f.isValid = isValid()
    return f
}

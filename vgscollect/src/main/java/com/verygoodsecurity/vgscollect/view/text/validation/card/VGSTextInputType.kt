package com.verygoodsecurity.vgscollect.view.text.validation.card

import java.util.regex.Pattern

sealed class VGSTextInputType {

    val name:String
        get() = when(this) {
            is CardNumber -> "card_num_type"
            is CardOwnerName -> "card_owner_type"
            is CVVCardCode -> "cvv_num_type"
            is CardExpDate -> "exp_date_type"
        }

    val length:Int
        get() = when(this) {
            is CardNumber -> 19
            is CardOwnerName -> 256
            is CVVCardCode -> 4
            is CardExpDate -> 7
        }

    open fun validate(str:String?):Boolean {
        val p = Pattern.compile(validation)

        return p.matcher(str.orEmpty()).matches()
    }

    protected abstract var validation:String

    class CardNumber : VGSTextInputType() {
        var card:CreditCardType = CreditCardType.Unknown
        override var validation: String
            get() = card.validationPattern
            set(_) {}

        override fun validate(str: String?): Boolean {
            card = getTypeCredit(str)
            validation = card.validationPattern
            return super.validate(str)
        }
    }

    object CVVCardCode : VGSTextInputType() {
        override var validation: String
            get() = "^[0-9]{3,4}\$"
            set(_) {}
    }
    object CardExpDate: VGSTextInputType() {
        override var validation: String
            get() = "^([01]|0[1-9]|1[012])[\\/]((19|20)\\d\\d|(2)\\d|(19))\$"
            set(_) {}
    }
    object CardOwnerName: VGSTextInputType() {
        override var validation: String
            get() = "^[\\p{L}\\s'.-]+\$"        //apply anything  -  "^[a-zA-Z0-9 ,]+\$"
            set(_) {}
    }
}
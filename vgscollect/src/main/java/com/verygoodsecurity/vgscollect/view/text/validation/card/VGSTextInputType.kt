package com.verygoodsecurity.vgscollect.view.text.validation.card

import java.util.regex.Pattern

sealed class VGSTextInputType {

    val name:String
        get() = when(this) {
            is CardNumber -> "card_num_type"
            is CardHolderName -> "card_holder_type"
            is CVCCardCode -> "cvc_num_type"
            is CardExpDate -> "exp_date_type"
        }

    val length:Int
        get() = when(this) {
            is CardNumber -> 19
            is CardHolderName -> 256
            is CVCCardCode -> 4
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
            val cardNumber = str?.replace(" ".toRegex(), "")
            card = getTypeCredit(cardNumber)

            validation = card.validationPattern
            return super.validate(cardNumber)
        }
    }

    object CVCCardCode : VGSTextInputType() {
        override var validation: String
            get() = "^[0-9]{3,4}\$"
            set(_) {}
    }
    object CardExpDate: VGSTextInputType() {
        override var validation: String
            get() = "^([01]|0[1-9]|1[012])[\\/]((19|20)\\d\\d|(2)\\d|(19))\$"
            set(_) {}
    }
    object CardHolderName: VGSTextInputType() {
        override var validation: String
            get() = "^[a-zA-Z0-9 ,]+\$"      //only symbols  -  "^[\\p{L}\\s'.-]+\$"
            set(_) {}
    }
}
package com.verygoodsecurity.vgscollect.view.text.validation.card

import java.util.regex.Pattern

sealed class CreditCardType {

    val name:String
        get() = when(this) {
            is Elo -> "Elo"                             //6362970000457013
            is VisaElectron -> "Visa electron"                 //4917300800000000
            is Maestro -> "Maestro"                            //6759649826438453
            is Forbrugsforeningen -> "Forbrugsforeningen"         //600722
            is Dankort -> "Dankort"                         //5019717010103742
            is Visa -> "Visa"                               //4111111111111111
            is Mastercard -> "MasterCard"                             //5555555555555555
            is AmericanExpress -> "American Express"                  //378282246310005
            is Hipercard -> "Hipercard"                             //6062826786276634
            is DinClub -> "Diners Club"                             //30043277253249
            is Discover -> "Discover"                            //6011000000000004
            is Jcb -> "JCB"                                     //3566002020360505
            is UnionPay -> "UnionPay International"             //6212345678901232
            is Laser -> "Laser"                             //670676038979126821
            is Unknown -> "Unknown"
        }

    abstract val validationPattern:String

    fun isValid(str:String?):Boolean {
        return Pattern.compile(validationPattern).matcher(str).matches()
    }

    object Elo:CreditCardType() {
        override val validationPattern: String
            get() = ""
    }
    object VisaElectron:CreditCardType() {
        override val validationPattern: String
            get() = ""
    }
    object Maestro:CreditCardType() {
        override val validationPattern: String
            get() = ""
    }
    object Forbrugsforeningen:CreditCardType() {
        override val validationPattern: String
            get() = ""
    }
    object Dankort:CreditCardType() {
        override val validationPattern: String
            get() = ""
    }

    object Visa : CreditCardType() {
        override val validationPattern: String
            get() = "^4[0-9]{12}(?:[0-9]{3})?\$"
    }
    object Mastercard:CreditCardType() {
        override val validationPattern: String
            get() = "^(?:5[1-5][0-9]{2}|222[1-9]|22[3-9][0-9]|2[3-6][0-9]{2}|27[01][0-9]|2720)[0-9]{12}\$"
    }


    object AmericanExpress:CreditCardType() {
        override val validationPattern: String
            get() = "^3[47][0-9]{13}\$"
    }

    object Hipercard :CreditCardType() {
        override val validationPattern: String
            get() = ""
    }

    object DinClub:CreditCardType() {
        override val validationPattern: String
            get() = "^3(?:0[0-5]|[68][0-9])[0-9]{11}\$"
    }
    object Discover:CreditCardType() {
        override val validationPattern: String
            get() = "^6(?:011|5[0-9]{2})[0-9]{12}\$"
    }

    object UnionPay:CreditCardType() {
        override val validationPattern: String
            get() = ""
    }

    object Jcb:CreditCardType() {
        override val validationPattern: String
            get() = "^(?:2131|1800|35\\d{3})\\d{11}\$"
    }

    object Laser:CreditCardType() {
        override val validationPattern: String
            get() = ""
    }
    object Unknown:CreditCardType() {
        override val validationPattern: String
            get() = "^\\b\$"
    }

}

fun getTypeCredit(str:String?):CreditCardType {
    if(str.isNullOrEmpty()) {
        return CreditCardType.Unknown
    }

    return when {
        CreditCardType.Visa.isValid(str) -> CreditCardType.Visa
        CreditCardType.Mastercard.isValid(str) -> CreditCardType.Mastercard
        CreditCardType.AmericanExpress.isValid(str) -> CreditCardType.AmericanExpress
        CreditCardType.DinClub.isValid(str) -> CreditCardType.DinClub
        CreditCardType.Discover.isValid(str) -> CreditCardType.Discover
        CreditCardType.Jcb.isValid(str) -> CreditCardType.Jcb
        else -> CreditCardType.Unknown
    }
}
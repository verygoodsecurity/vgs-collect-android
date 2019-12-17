package com.verygoodsecurity.vgscollect.view.card.validation.card.brand

import com.verygoodsecurity.vgscollect.view.card.validation.VGSValidator

class VisaDelegate : VGSValidator by DefaultCardValidator("^4[0-9]{12}((?:[0-9]{3})?|(?:[0-9]{6})?)\$")





//object Visa : CreditCardType() {    //4111111111111111
//    override val validationPattern: String
//        get() = "^4[0-9]{12}(?:[0-9]{3})?\$"
//}
//object Mastercard:CreditCardType() {    //5555555555555555
//    override val validationPattern: String
//        get() = "^(?:5[1-5][0-9]{2}|222[1-9]|22[3-9][0-9]|2[3-6][0-9]{2}|27[01][0-9]|2720)[0-9]{12}\$"
//}
//object AmericanExpress:CreditCardType() {   //378282246310005
//    override val validationPattern: String
//        get() = "^3[47][0-9]{13}\$"
//}
//object DinClub:CreditCardType() {   //30043277253249
//    override val validationPattern: String
//        get() = "^3(?:0[0-5]|[68][0-9])[0-9]{11}\$"
//}
//object Discover:CreditCardType() {  //6011000000000004
//    override val validationPattern: String
//        get() = "^6(?:011|5[0-9]{2})[0-9]{12}\$"
//}
//object Jcb:CreditCardType() {   //3566002020360505
//    override val validationPattern: String
//        get() = "^(?:2131|1800|35\\d{3})\\d{11}\$"
//}
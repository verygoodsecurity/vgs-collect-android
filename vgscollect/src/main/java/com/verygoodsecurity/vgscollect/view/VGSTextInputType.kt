package com.verygoodsecurity.vgscollect.view

sealed class VGSTextInputType {

    open val name:String = ""

    object CardNumber : VGSTextInputType() {
        const val length:Int = 19
        override val name:String = "cardNumber"
    }
    object CVVCardCode : VGSTextInputType() {
        const val length:Int = 3
        override val name:String = "cvvNum"
    }
    object CardExpDate:VGSTextInputType() {
        const val length:Int = 5
        override val name:String = "expDate"
    }
    object CardOwnerName:VGSTextInputType() {
        const val length:Int = 256
        override val name:String = "cardOwner"
    }
    object InfoField:VGSTextInputType() {
        const val length:Int = Int.MAX_VALUE
        override val name:String = "none"
    }
}
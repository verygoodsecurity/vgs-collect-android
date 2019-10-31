package com.verygoodsecurity.vgscollect.view

sealed class VGSTextInputType {
    object CardNumber : VGSTextInputType() { const val length:Int = 19 }
    object CVVCardCode : VGSTextInputType() { const val length:Int = 3 }
    object CardExpDate:VGSTextInputType() { const val length:Int = 5 }
    object CardOwnerName:VGSTextInputType() { const val length:Int = 256 }
    object InfoField:VGSTextInputType() { const val length:Int = Int.MAX_VALUE }
}
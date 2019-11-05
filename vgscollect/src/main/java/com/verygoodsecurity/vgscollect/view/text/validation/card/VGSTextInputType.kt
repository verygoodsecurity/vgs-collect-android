package com.verygoodsecurity.vgscollect.view.text.validation.card

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

    val validation:String
        get() = when(this) {
            is CardNumber -> "^(?:5[1-5][0-9]{2}|222[1-9]|22[3-9][0-9]|2[3-6][0-9]{2}|27[01][0-9]|2720)[0-9]{12}\$"
            is CardOwnerName -> "^[\\p{L}\\s'.-]+\$"
            is CVVCardCode -> "^[0-9]{3,4}\$"
            is CardExpDate -> "^([01]|0[1-9]|1[012])[\\/]((19|20)\\d\\d|(2)\\d|(19))\$"
        }

    object CardNumber : VGSTextInputType()
    object CVVCardCode : VGSTextInputType()
    object CardExpDate: VGSTextInputType()
    object CardOwnerName: VGSTextInputType()
}
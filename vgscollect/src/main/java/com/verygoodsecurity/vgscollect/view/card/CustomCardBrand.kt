package com.verygoodsecurity.vgscollect.view.card

data class CustomCardBrand(
    val regex:String,
    val cardBrandName:String,
    val mask:String = "#### #### #### ####",
    val drawableResId:Int = 0
)
package com.verygoodsecurity.vgscollect.view.card.filter

interface VGSCardFilter {
    fun detect(str:String?):CardBrandWrapper?
}
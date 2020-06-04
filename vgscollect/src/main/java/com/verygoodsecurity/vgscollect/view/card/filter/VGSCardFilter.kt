package com.verygoodsecurity.vgscollect.view.card.filter

/** @suppress */
interface VGSCardFilter {
    fun detect(str:String?):CardBrandPreview?
}
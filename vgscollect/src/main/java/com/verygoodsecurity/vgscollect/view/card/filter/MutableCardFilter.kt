package com.verygoodsecurity.vgscollect.view.card.filter

import com.verygoodsecurity.vgscollect.view.card.CustomCardBrand

interface MutableCardFilter : VGSCardFilter {
    fun add(item: CustomCardBrand?)
}
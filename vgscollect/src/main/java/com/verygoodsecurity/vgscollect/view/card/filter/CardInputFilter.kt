package com.verygoodsecurity.vgscollect.view.card.filter

interface CardInputFilter {
    fun clearFilters()
    fun addFilter(filter: VGSCardFilter?)
}
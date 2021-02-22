package com.verygoodsecurity.vgscollect.view.card.filter

/** @suppress */
interface CardInputFilter {
    fun clearFilters()
    fun addFilter(filter: VGSCardFilter?)
}
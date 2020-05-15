package com.verygoodsecurity.vgscollect.core.storage

/** @suppress */
interface StorageContractor<T> {
    fun checkState(state:T):Boolean
}
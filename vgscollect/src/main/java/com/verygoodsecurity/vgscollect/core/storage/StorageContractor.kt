package com.verygoodsecurity.vgscollect.core.storage

interface StorageContractor<T> {
    fun checkState(state:T):Boolean
}
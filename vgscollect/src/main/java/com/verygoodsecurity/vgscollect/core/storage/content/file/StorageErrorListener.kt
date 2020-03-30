package com.verygoodsecurity.vgscollect.core.storage.content.file

internal interface StorageErrorListener {
    fun onStorageError(messageResId:Int)
}
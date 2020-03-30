package com.verygoodsecurity.vgscollect.core.storage.content.file

import com.verygoodsecurity.vgscollect.core.api.VGSError

internal interface StorageErrorListener {
    fun onStorageError(error: VGSError)
}
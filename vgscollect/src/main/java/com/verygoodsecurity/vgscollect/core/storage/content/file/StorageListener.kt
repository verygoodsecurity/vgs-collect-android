package com.verygoodsecurity.vgscollect.core.storage.content.file

import com.verygoodsecurity.vgscollect.core.model.network.VGSError

internal interface StorageListener {

    fun onStorageError(error: VGSError, vararg params: String?)
}
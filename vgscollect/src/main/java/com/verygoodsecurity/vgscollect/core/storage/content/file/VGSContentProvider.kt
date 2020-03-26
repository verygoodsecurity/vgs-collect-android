package com.verygoodsecurity.vgscollect.core.storage.content.file

import com.verygoodsecurity.vgscollect.core.model.state.FileState

interface VGSContentProvider {
    fun attachFile(fieldName : String)

    fun getAttachedFiles():List<FileState>

    fun detachAll()
    fun detachFile(file: FileState)
}
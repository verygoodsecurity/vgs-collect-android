package com.verygoodsecurity.vgscollect.core.storage.content.file

interface VGSContentProvider {
    fun attachFile()

    fun getAttachedFiles():List<FileData>

    fun detachAll()
    fun detachFile(file: FileData)
}
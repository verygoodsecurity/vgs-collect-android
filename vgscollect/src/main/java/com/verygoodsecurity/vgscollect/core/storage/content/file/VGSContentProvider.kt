package com.verygoodsecurity.vgscollect.core.storage.content.file

interface VGSContentProvider {
    fun attachFile(fieldName : String)

    fun getAttachedFiles():List<FileData>

    fun detachAll()
    fun detachFile(file: FileData)
}
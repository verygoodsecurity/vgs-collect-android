package com.verygoodsecurity.vgscollect.core.storage.content.file

data class FileData(
    val size:Long = 0,
    val name:String?,
    val mimeType:String?,
    val fieldName:String
) {
    override fun hashCode(): Int {
        return fieldName.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is FileData && other.hashCode() == hashCode()
    }
}
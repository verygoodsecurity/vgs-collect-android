package com.verygoodsecurity.vgscollect.core.model.state

data class FileState(
    val size:Long = 0,
    val name:String?,
    val mimeType:String?,
    val fieldName:String
) {
    override fun hashCode(): Int {
        return fieldName.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is FileState && other.hashCode() == hashCode()
    }
}
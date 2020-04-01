package com.verygoodsecurity.vgscollect.core.model.state

/**
 * The base class definition for a file presentation state.
 *
 * @since 1.1.0
 */
data class FileState(
    /**  The size of a file which it takes up on your device */
    val size:Long = 0,
    /** The name of the file */
    val name:String?,
    /** The MIME type ot the file */
    val mimeType:String?,
    /**
     * The text to be used for data transfer to VGS proxy.
     * Usually, it is similar to field-name in JSON path in your inbound route filters.
     */
    val fieldName:String
) {
    override fun hashCode(): Int {
        return fieldName.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is FileState && other.hashCode() == hashCode()
    }
}
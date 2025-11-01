package com.verygoodsecurity.vgscollect.core.model.state

/**
 * The state of a file attached to the VGS Collect instance.
 *
 * @param size The size of the file in bytes.
 * @param name The name of the file.
 * @param mimeType The MIME type of the file.
 * @param fieldName The name of the field used for data transfer to the VGS proxy.
 */
data class FileState(
    val size: Long = 0,
    val name: String?,
    val mimeType: String?,
    val fieldName: String
) {

    override fun hashCode(): Int = fieldName.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as FileState
        if (fieldName != other.fieldName) return false
        return true
    }
}
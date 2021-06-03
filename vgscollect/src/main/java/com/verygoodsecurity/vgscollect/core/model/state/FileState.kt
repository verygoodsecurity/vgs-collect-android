package com.verygoodsecurity.vgscollect.core.model.state

/**
 * The base class definition for a file presentation state.
 *
 * @param size The size of a file which it takes up on your device.
 * @param name The name of the file.
 * @param mimeType The size of a file which it takes up on your device.
 * @param fieldName The text to be used for data transfer to VGS proxy. Usually, it is similar to field-name in JSON path in your inbound route filters.
 * @constructor Primary constructor
 *
 * @since 1.1.0
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
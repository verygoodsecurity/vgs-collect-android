package com.verygoodsecurity.api.nfc.core.content

enum class TrailerADPU(
    private val statusBytes: String
) {
    /** No information given */
    SW_6500("6500"),

    /** Command successfully executed (OK) */
    SW_9000("9000");

    fun getStatus(): ByteArray {
        return statusBytes.toHexByteArray()
    }

    private fun String.toHexByteArray(): ByteArray {    //fixme find&reuse method to some native/kotlin utils
        return run {
            val text = this.replace(" ", "")
            if (text.length % 2 != 0) {
                throw IllegalArgumentException("Hex binary needs to be even-length :$this")
            } else {
                val commandByte = ByteArray(Math.round(text.length.toFloat() / 2.0f))
                var j = 0
                var i = 0
                while (i < text.length) {
                    val `val` = Integer.valueOf(text.substring(i, i + 2).toInt(16))
                    commandByte[j++] = `val`.toByte()
                    i += 2
                }
                commandByte
            }
        }
    }

}
package com.verygoodsecurity.api.nfc.core.content

internal data class CommandAPDU(

    /** Class byte **/
    var cla: Int = 0x00,

    /**
     * Instruction byte
     * 0xA4:Select Command
     * 0xB2:Read Record Command
     */
    var ins: Int = 0x00,

    /**
     * Parameter 1 byte
     * The value and meaning depends on the instruction code (INS).
     */
    var p1: Int = 0x00,

    /**
     * Parameter 2 byte
     * The value and meaning depends on the instruction code (INS).
     */
    var p2: Int = 0x00,

    /**
     * Number of data bytes send to the card.
     * With the Smart Card Shell the value of Lc will be calculated automatically.
     * You don't have to specify this parameter.
     */
    var lc: Int = 0x00,

    /**
     * Data bytes
     */
    var data: ByteArray? = ByteArray(0),

    /**
     * Number of data bytes expected in the response. If Le is 0x00, at maximum 256 bytes are expected.
     */
    var le: Int = 0x00,
    var leUsed: Boolean = false
) {

    constructor(
        command: CommandEnum,
        data: ByteArray?,
        le: Int = 0
    ) : this(command.cla, command.ins, command.p1, command.p2, data?.size ?: 0, data, le, true)

    fun toBytes(): ByteArray {
        var length = 4 // CLA, INS, P1, P2

        data?.takeIf { it.isNotEmpty() }
            ?.let {
                length += 1 // LC
                length += it.size
            }

        if (leUsed) {
            length += 1 // LE
        }

        val apdu = ByteArray(length)

        var index = 0
        apdu[index] = cla.toByte()
        index++
        apdu[index] = ins.toByte()
        index++
        apdu[index] = p1.toByte()
        index++
        apdu[index] = p2.toByte()
        index++

        data?.takeIf { it.isNotEmpty() }
            ?.let {
                apdu[index] = lc.toByte()
                index++
                System.arraycopy(it, 0, apdu, index, it.size)
                index += it.size
            }

        if (leUsed) {
            val value = (apdu[index] + le).toByte()
            apdu[index] = value
        }

        return apdu
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CommandAPDU

        if (cla != other.cla) return false
        if (ins != other.ins) return false
        if (p1 != other.p1) return false
        if (p2 != other.p2) return false
        if (lc != other.lc) return false
        if (data != null) {
            if (other.data == null) return false
            if (!data.contentEquals(other.data)) return false
        } else if (other.data != null) return false
        if (le != other.le) return false
        if (leUsed != other.leUsed) return false

        return true
    }

    override fun hashCode(): Int {
        var result = cla
        result = 31 * result + ins
        result = 31 * result + p1
        result = 31 * result + p2
        result = 31 * result + lc
        result = 31 * result + (data?.contentHashCode() ?: 0)
        result = 31 * result + le
        result = 31 * result + leUsed.hashCode()
        return result
    }

}
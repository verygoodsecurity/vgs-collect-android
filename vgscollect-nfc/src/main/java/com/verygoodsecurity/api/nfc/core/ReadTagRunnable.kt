package com.verygoodsecurity.api.nfc.core

import android.nfc.Tag
import com.verygoodsecurity.api.nfc.core.content.*
import com.verygoodsecurity.api.nfc.core.model.Card
import com.verygoodsecurity.api.nfc.core.utils.*

internal class ReadTagRunnable(
    @Suppress("unused") private val tag: Tag,
    private val listener: ResultListener,
) : Runnable {

    private val provider = IsoDepProvider(tag)

    override fun run() {
        provider.connect()

        provider.transceive(
            CommandAPDU(CommandEnum.SELECT, PPSE).toBytes()
        )?.takeIf { it.isSuccessful() }
            ?.run { parseFCITemplate(this) }
            ?.takeIf { it.isSuccessful() }
            ?.run { extractCardData(this) }
            ?.also { listener.onSuccess(it) } ?: listener.onFailure("error")
    }

    fun parseFCITemplate(source: ByteArray): ByteArray {
        // Get SFI
        return source.getTLVValue(EMV.SFI)?.run {
            // Check SFI
            val sfi = byteArrayToInt()
            val fciCommand = CommandAPDU(CommandEnum.READ_RECORD, sfi, sfi shl 3 or 4, 0).toBytes()
            val d2 = provider.transceive(fciCommand)
            // If LE is not correct
            if (d2 != null && compareADPU(TrailerADPU.SW_6C)) {
                val leCommand = CommandAPDU(
                    CommandEnum.READ_RECORD, sfi, sfi shl 3 or 4, this[size - 1].toInt()
                ).toBytes()
                return provider.transceive(leCommand)!!
            }
            this
        } ?: source
    }

    fun extractCardData(data: ByteArray): Card? {
        var card: Card? = null
        val aids: List<ByteArray> = data.getAids()
        for (aid in aids) {
            val label: String = extractApplicationLabel(data)
            if (label.isNotEmpty()) {
                card = Card(label, "4242424242424242", "10/29")
                break
            }
        }
        return card
    }

    private fun extractApplicationLabel(pData: ByteArray?): String {
        var label = ""
        val labelByte = pData?.getTLVValue(EMV.APPLICATION_LABEL)
        if (labelByte != null) {
            label = String(labelByte)
        }
        return label
    }

    interface ResultListener {

        fun onSuccess(card: Card)

        fun onFailure(error: String)

    }

    companion object {

        /**
         * PPSE directory "2PAY.SYS.DDF01"
         */
        private val PPSE = "2PAY.SYS.DDF01".toByteArray()
    }

}
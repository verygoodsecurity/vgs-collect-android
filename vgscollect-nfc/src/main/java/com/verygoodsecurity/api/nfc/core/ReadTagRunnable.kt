package com.verygoodsecurity.api.nfc.core

import android.nfc.Tag
import android.util.Log
import com.verygoodsecurity.api.nfc.core.content.*
import com.verygoodsecurity.api.nfc.core.model.Card
import com.verygoodsecurity.api.nfc.core.utils.*
import java.util.*

internal class ReadTagRunnable(
    @Suppress("unused") private val tag: Tag,
    private val listener: ResultListener,
) : Runnable {

    private val provider = IsoDepProvider(tag)
    private val cardAdapter = CardAdapter()


    override fun run() {
        provider.connect()

        val command = CommandAPDU(CommandEnum.SELECT, PPSE).toBytes()

        provider.transceive(command)
            .takeIf {
                it.isSucceed()
            }?.apply {
                getTLVValue(EMV.SFI)
            }?.run {
                // Check SFI
                val sfi = byteArrayToInt()

                val parseFCIProprietaryCommand = CommandAPDU(CommandEnum.READ_RECORD, sfi, sfi shl 3 or 4, 0).toBytes()
                val data = provider.transceive(parseFCIProprietaryCommand)

                // If LE is not correct
                if (data!= null && data.compareADPU(TrailerADPU.SW_6C)) {
                    val parseLECommand2 = CommandAPDU(
                        CommandEnum.READ_RECORD,
                        sfi,
                        sfi shl 3 or 4,
                        data[data.size - 1].toInt()
                    ).toBytes()

                    return@run provider.transceive(parseLECommand2)
                } else {
                    return@run this
                }
            }?.takeIf {
                it.isSucceed()
            }?.run {
                // Get Aids
                val aids: List<ByteArray> = getAids()
                for (aid in aids) {
                    val label:String? = extractApplicationLabel(this)
                    Log.e("test", label.toString())
//                    extractPublicData()
                }
                this
            }?.run {
                cardAdapter.getCard(this)
            }?.let {
                listener.onSuccess(it)
            } ?: listener.onFailure("error")
    }

    protected fun extractApplicationLabel(pData: ByteArray?): String? {
        var label: String? = null
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
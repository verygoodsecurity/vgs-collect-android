package com.verygoodsecurity.api.nfc.core

import android.nfc.Tag
import com.verygoodsecurity.api.nfc.core.content.CardAdapter
import com.verygoodsecurity.api.nfc.core.content.CommandAPDU
import com.verygoodsecurity.api.nfc.core.content.CommandEnum
import com.verygoodsecurity.api.nfc.core.content.IsoDepProvider
import com.verygoodsecurity.api.nfc.core.model.Card
import com.verygoodsecurity.api.nfc.core.utils.isSucceed

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
            }?.run {
                cardAdapter.getCard(this)
            }?.let {
                listener.onSuccess(it)
            } ?: listener.onFailure("error")
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
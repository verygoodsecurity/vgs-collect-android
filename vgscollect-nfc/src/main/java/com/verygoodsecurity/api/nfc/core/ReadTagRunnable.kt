package com.verygoodsecurity.api.nfc.core

import android.nfc.Tag
import com.verygoodsecurity.api.nfc.core.model.Card

internal class ReadTagRunnable(
    @Suppress("unused") private val tag: Tag,
    private val listener: ResultListener,
) : Runnable {

    override fun run() {
        // TODO: Implement reading tag logic here(or in separate class)
        listener.onSuccess(Card("8567123456329012", "10/22"))
    }

    interface ResultListener {

        fun onSuccess(card: Card)

        fun onFailure(error: String)
    }
}
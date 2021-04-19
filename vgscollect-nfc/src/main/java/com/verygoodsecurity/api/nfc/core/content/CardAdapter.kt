package com.verygoodsecurity.api.nfc.core.content

import com.verygoodsecurity.api.nfc.core.model.Card

internal class CardAdapter {

    fun getCard(data: ByteArray?): Card? {
        return Card("8567123456329012", "10/22")
    }

}
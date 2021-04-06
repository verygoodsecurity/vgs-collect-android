package com.verygoodsecurity.api.nfc.core

import android.nfc.Tag
import com.verygoodsecurity.api.nfc.core.model.Card

internal class PaymentAsyncTask(tag: Tag) {
    fun execute(): Card {
        return Card("41", "12/12")
    }
}
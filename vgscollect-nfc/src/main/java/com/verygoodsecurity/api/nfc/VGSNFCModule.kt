package com.verygoodsecurity.api.nfc

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.NfcA
import com.verygoodsecurity.api.nfc.core.PaymentAsyncTask
import com.verygoodsecurity.vgscollect.view.card.FieldType

class VGSNFCModule(
    private val activity: Activity
) : VGSCardNFCAdapter() {
    private val INTENT_FILTER = arrayOf(
        IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED),
        IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
    )

    private val TECH_LIST = arrayOf(
        arrayOf(
            NfcA::class.java.name, IsoDep::class.java.name
        )
    )

    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var mPendingIntent: PendingIntent

    override fun initialize() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(activity)
        mPendingIntent = PendingIntent.getActivity(
            activity, 0,
            Intent(activity, activity.javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
        )
    }

    override fun startScanning() {
        nfcAdapter.enableForegroundDispatch(activity, mPendingIntent, INTENT_FILTER, TECH_LIST)
    }

    override fun stopScanning() {
        nfcAdapter.disableForegroundDispatch(activity)
    }

    override fun navigateUpTo(i: Intent?) {
        val tag: Tag? = i?.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        if (nfcAdapter.isEnabled && tag != null) {
            PaymentAsyncTask(tag).execute().also { card ->
                fields.forEach {
                    when (it.value) {
                        FieldType.CARD_NUMBER -> mapData(it.key, card.number)
                        FieldType.CARD_EXPIRATION_DATE -> mapData(it.key, card.date)
                    }
                }
                send()
            }
        }
    }
}
package com.verygoodsecurity.api.nfc

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.NfcA
import com.verygoodsecurity.api.nfc.core.ReadTagRunnable
import com.verygoodsecurity.api.nfc.core.model.Card

class VGSNFCAdapter(private val activity: Activity) : NFCAdapter() {

    private var nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(activity)
    private var adapterIntent = createAdapterPendingIntent()

    override fun startReading() {
        nfcAdapter?.enableForegroundDispatch(activity, adapterIntent, INTENT_FILTER, TECH_LIST)
            ?: notifyDataReceiveFailed("Adapter is not initialized because device is does not support NFC of it disabled")
    }

    override fun stopReading() {
        nfcAdapter?.disableForegroundDispatch(activity)
    }

    override fun handleNewIntent(intent: Intent?) {
        (intent?.getParcelableExtra(NfcAdapter.EXTRA_TAG) as? Tag)?.let {
            ReadTagRunnable(it, object : ReadTagRunnable.ResultListener {

                override fun onSuccess(card: Card) {
//                    notifyDataReceived()
                }

                override fun onFailure(error: String) {
                    notifyDataReceiveFailed(error)
                }
            })
        }
    }

    private fun createAdapterPendingIntent(): PendingIntent {
        val intent = Intent(activity, activity.javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        return PendingIntent.getActivity(activity, 0, intent, 0)
    }

    companion object {

        private val TECH_LIST = arrayOf(arrayOf(NfcA::class.java.name, IsoDep::class.java.name))

        private val INTENT_FILTER = arrayOf(
            IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED),
            IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        )
    }
}
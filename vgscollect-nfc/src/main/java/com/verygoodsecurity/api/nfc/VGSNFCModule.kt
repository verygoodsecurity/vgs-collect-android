package com.verygoodsecurity.api.nfc

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.NfcA
import android.util.Log
import com.verygoodsecurity.api.nfc.core.ReadTagRunnable
import com.verygoodsecurity.api.nfc.core.model.Card
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

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

    private var readTask: Future<*>? = null
    private val readTaskExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    override fun navigateUpTo(i: Intent?) {
        (i?.getParcelableExtra(NfcAdapter.EXTRA_TAG) as? Tag)?.let {
            readTask?.cancel(true)
            readTask = readTaskExecutor.submit(
                ReadTagRunnable(it, object : ReadTagRunnable.ResultListener {
                    override fun onSuccess(card: Card) {
                        Log.e("test", "onSuccess")
                    }
                    override fun onFailure(error: String) {
                        Log.e("test", "onFailure")
                    }
                })
            )
        }
    }
}
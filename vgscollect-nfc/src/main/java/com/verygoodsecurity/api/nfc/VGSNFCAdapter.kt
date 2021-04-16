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
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * Provides ability to read NFC tags data into VGS views.
 */
class VGSNFCAdapter(
    private val activity: Activity,
    private val dataMapper: VGSNFCDataMapper
) : NFCAdapter() {

    private var nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(activity)
    private var nfcAdapterIntent = createAdapterPendingIntent()

    private var readTask: Future<*>? = null
    private val readTaskExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    /**
     * Enable NFC tags scanning.
     * This function should be called only the activity is in the foreground (resumed) state.
     * Also, disableForegroundDispatch must be called before the completion of [Activity.onPause]
     */
    override fun enableForegroundDispatch() {
        nfcAdapter?.enableForegroundDispatch(activity, nfcAdapterIntent, INTENT_FILTER, TECH_LIST)
            ?: notifyReadingFailed("NFC Adapter is not initialized because device is does not support NFC or it disabled")
    }

    /**
     * Disable NFC tags scanning.
     * Must be called before the completion of [Activity.onPause]
     */
    override fun disableForegroundDispatch() {
        readTask?.cancel(true)
        nfcAdapter?.disableForegroundDispatch(activity)
    }

    /**
     * Must call this function inside [Activity.onNewIntent] to be able to read tag data.
     */
    override fun onNewIntent(intent: Intent?) {
        (intent?.getParcelableExtra(NfcAdapter.EXTRA_TAG) as? Tag)?.let {
            readTask?.cancel(true)
            readTask = readTaskExecutor.submit(
                ReadTagRunnable(it, object : ReadTagRunnable.ResultListener {

                    override fun onSuccess(card: Card) {
                        setData(dataMapper.map(card))
                        notifyReadingSuccess()
                    }

                    override fun onFailure(error: String) {
                        notifyReadingFailed(error)
                    }
                })
            )
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
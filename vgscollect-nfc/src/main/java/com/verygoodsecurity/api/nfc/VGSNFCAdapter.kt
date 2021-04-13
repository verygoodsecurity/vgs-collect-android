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
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.card.FieldType
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class VGSNFCAdapter(
    private val activity: Activity,
    private vararg val fields: InputFieldView
) : NFCAdapter() {

    private var nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(activity)
    private var nfcAdapterIntent = createAdapterPendingIntent()

    private var readTask: Future<*>? = null
    private val readTaskExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    override fun startReading() {
        nfcAdapter?.enableForegroundDispatch(activity, nfcAdapterIntent, INTENT_FILTER, TECH_LIST)
            ?: notifyReadingFailed("NFC Adapter is not initialized because device is does not support NFC or it disabled")
    }

    override fun stopReading() {
        readTask?.cancel(true)
        nfcAdapter?.disableForegroundDispatch(activity)
    }

    override fun handleNewIntent(intent: Intent?) {
        (intent?.getParcelableExtra(NfcAdapter.EXTRA_TAG) as? Tag)?.let {
            readTask?.cancel(true)
            readTask = readTaskExecutor.submit(
                ReadTagRunnable(it, object : ReadTagRunnable.ResultListener {

                    override fun onSuccess(card: Card) {
                        setData(mapCardToFields(card))
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

    private fun mapCardToFields(card: Card): Map<String, Any?> {
        val data = mutableMapOf<String, Any?>()
        fields.forEach {
            it.getFieldName()?.let { fieldName ->
                when (it.getFieldType()) {
                    FieldType.CARD_NUMBER -> data[fieldName] = card.number
                    FieldType.CARD_EXPIRATION_DATE -> data[fieldName] = card.date
                    else -> TODO("Not implemented yet!")
                }
            }
        }
        return data
    }

    companion object {

        private val TECH_LIST = arrayOf(arrayOf(NfcA::class.java.name, IsoDep::class.java.name))

        private val INTENT_FILTER = arrayOf(
            IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED),
            IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        )
    }
}
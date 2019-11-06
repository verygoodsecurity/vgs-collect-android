package com.verygoodsecurity.vgscollect.core

import android.app.Activity
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.util.Log
import android.webkit.URLUtil
import androidx.core.content.ContextCompat
import com.verygoodsecurity.vgscollect.core.model.SimpleResponse
import com.verygoodsecurity.vgscollect.core.model.mapToEncodedQuery
import com.verygoodsecurity.vgscollect.core.storage.DefaultStorage
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import com.verygoodsecurity.vgscollect.widget.VGSEditText
import java.lang.StringBuilder
import java.lang.ref.WeakReference

class VGSCollect(id:String, environment: Environment = Environment.SANDBOX) {

    private val baseURL:String
    private val isURLValid:Boolean

    private val storage = DefaultStorage()
    private val client = ApiClient()
    private val tasks = mutableListOf<AsyncTask<String?, Void, SimpleResponse>>()

    private companion object {
        private const val DOMEN = "verygoodproxy.com"
        private const val DIVIDER = "."
        private const val SCHEME  = "https://"
    }

    var onResponceListener:VgsCollectResponseListener? = null
    var onFieldStateChangeListener: OnFieldStateChangeListener? = null
        set(value) {
            field = value
            storage.onFieldStateChangeListener = value
        }

    init {
        val builder = StringBuilder(SCHEME)
            .append(id).append(DIVIDER)
            .append(environment.rawValue).append(DIVIDER)
            .append(DOMEN)

        baseURL = builder.toString()
        isURLValid = URLUtil.isValidUrl(baseURL)
    }

    fun bindView(view: VGSEditText?) {
        val listener = storage.performSubscription()
        view?.inputField?.stateListener = listener
    }

    fun onDestroy() {
        storage.onFieldStateChangeListener = null
        onFieldStateChangeListener = null
        onResponceListener = null
        tasks.forEach {
            it.cancel(true)
        }
        tasks.clear()
        storage.clear()
    }

    fun submit(mainActivity:Activity) {
        when {
            ContextCompat.checkSelfPermission(mainActivity,android.Manifest.permission.INTERNET)
                    == PackageManager.PERMISSION_DENIED ->
                Log.e("VGSCollect", "Permission denied (missing INTERNET permission?)")
            !isURLValid -> Log.e("VGSCollect", "URL is not valid")
            !isValidData() -> return
            else -> performRequest()
        }
    }

    private fun isValidData(): Boolean {
        var isValid = true
        storage.getStates().forEach {
            if(!it.isValid()) {
                val r = SimpleResponse("is not a valid ${it.alias}", -1)
                onResponceListener?.onResponse(r)
                isValid = false
                return@forEach
            }
        }
        return isValid
    }

    private fun performRequest() {
        val states = storage.getStates()

        val operation = NetworkOperation(onResponceListener)
        tasks.add(operation)

        val data = states.mapToEncodedQuery()
        operation.execute(data)
    }

    private inner class NetworkOperation(listener: VgsCollectResponseListener?) : AsyncTask<String?, Void, SimpleResponse>() {

        var onResponceListener: WeakReference<VgsCollectResponseListener>? = WeakReference<VgsCollectResponseListener>(listener)

        override fun doInBackground(vararg arg: String?): SimpleResponse? {
            if(arg.isNotEmpty()) {
                val response = arg[0]?.run {
                    client.callPost(baseURL, this)
                }
                if(response != null) {
                    return response
                }
            }
            return null
        }

        override fun onPostExecute(result: SimpleResponse?) {
            super.onPostExecute(result)
            if(onResponceListener == null) {
                Log.i("VGSCollect", "VgsCollectResponseListener not set")
            } else {
                onResponceListener?.get()?.onResponse(result)
            }

            tasks.remove(this@NetworkOperation)
        }
    }
}
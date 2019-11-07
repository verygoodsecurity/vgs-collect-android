package com.verygoodsecurity.vgscollect.core

import android.app.Activity
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.webkit.URLUtil
import androidx.core.content.ContextCompat
import com.verygoodsecurity.vgscollect.core.api.ApiClient
import com.verygoodsecurity.vgscollect.core.api.Payload
import com.verygoodsecurity.vgscollect.core.api.URLConnectionClient
import com.verygoodsecurity.vgscollect.core.model.SimpleResponse
import com.verygoodsecurity.vgscollect.core.model.mapUsefulPayloads
import com.verygoodsecurity.vgscollect.core.storage.DefaultStorage
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import com.verygoodsecurity.vgscollect.util.Logger
import com.verygoodsecurity.vgscollect.widget.VGSEditText
import java.lang.StringBuilder
import java.lang.ref.WeakReference

class VGSCollect(id:String, environment: Environment = Environment.SANDBOX) {

    private val baseURL:String
    private val isURLValid:Boolean

    private val storage = DefaultStorage()
    private val client:ApiClient

    private val tasks = mutableListOf<AsyncTask<Payload?, Void, SimpleResponse>>()

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

        client = URLConnectionClient(baseURL)
    }

    fun bindView(view: VGSEditText?) {
        val listener = storage.performSubscription()
        view?.inputField?.stateListener = listener
    }

    fun onDestroy() {
        onFieldStateChangeListener = null
        onResponceListener = null
        tasks.forEach {
            it.cancel(true)
        }
        tasks.clear()
        storage.clear()
    }

    fun submit(mainActivity:Activity
               , path:String
               , method:HTTPMethod = HTTPMethod.POST
               , headers:Map<String,String>? = null
    ) {
        when {
            ContextCompat.checkSelfPermission(mainActivity,android.Manifest.permission.INTERNET)
                    == PackageManager.PERMISSION_DENIED ->
                Logger.e("VGSCollect", "Permission denied (missing INTERNET permission?)")
            !isURLValid -> Logger.e("VGSCollect", "URL is not valid")
            !isValidData() -> return
            else -> performRequest(path, method, headers)
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

    private fun performRequest( path: String,
                                method: HTTPMethod,
                                headers: Map<String, String>?) {
        val operation = NetworkOperation(client, onResponceListener)
        tasks.add(operation)

        val p = Payload(path, method, headers, storage.getStates().mapUsefulPayloads())
        operation.execute(p)
    }

    private inner class NetworkOperation(client:ApiClient, listener: VgsCollectResponseListener?) : AsyncTask<Payload?, Void, SimpleResponse>() {

        var onResponceListener: WeakReference<VgsCollectResponseListener>? = WeakReference<VgsCollectResponseListener>(listener)
        var client: WeakReference<ApiClient>? = WeakReference(client)

        override fun doInBackground(vararg arg: Payload?): SimpleResponse? {
            if(arg.isNotEmpty()) {
                val response = arg[0]?.run {
                    client?.get()?.call(path, method, headers, data)
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
                Logger.i("VGSCollect", "VgsCollectResponseListener not set")
            } else {
                onResponceListener?.get()?.onResponse(result)
            }

            tasks.remove(this@NetworkOperation)
        }
    }
}
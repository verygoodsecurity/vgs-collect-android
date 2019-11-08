package com.verygoodsecurity.vgscollect.core

import android.app.Activity
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.webkit.URLUtil
import androidx.core.content.ContextCompat
import com.verygoodsecurity.vgscollect.core.api.ApiClient
import com.verygoodsecurity.vgscollect.core.model.Payload
import com.verygoodsecurity.vgscollect.core.api.URLConnectionClient
import com.verygoodsecurity.vgscollect.core.model.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.core.model.mapUsefulPayloads
import com.verygoodsecurity.vgscollect.core.model.state.mapToFieldState
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

    private val tasks = mutableListOf<AsyncTask<Payload?, Void, VGSResponse>>()

    private companion object {
        private const val DOMEN = "verygoodproxy.com"
        private const val DIVIDER = "."
        private const val SCHEME  = "https://"
    }

    var onResponseListener:VgsCollectResponseListener? = null

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
        if(view is VGSEditText) {
            val listener = storage.performSubscription()
            view.inputField.stateListener = listener
        }
    }

    fun onDestroy() {
        onResponseListener = null
        tasks.forEach {
            it.cancel(true)
        }
        tasks.clear()
        storage.clear()
    }

    fun addOnFieldStateChangeListener(listener: OnFieldStateChangeListener?) {
        storage.onFieldStateChangeListener = listener
    }

    fun getAllStates() = storage.getStates().map { it.mapToFieldState() }

    fun submit(mainActivity:Activity
               , path:String
               , method:HTTPMethod = HTTPMethod.POST
               , headers:Map<String,String>? = null
    ) {
        appValidationCheck(mainActivity) { data ->
            doInCurrentThread(path, method, headers, data)
        }
    }

    private fun appValidationCheck(mainActivity: Activity, func: ( data: MutableCollection<VGSFieldState>) -> Unit) {
        when {
            ContextCompat.checkSelfPermission(mainActivity,android.Manifest.permission.INTERNET)
                    == PackageManager.PERMISSION_DENIED ->
                Logger.e("VGSCollect", "Permission denied (missing INTERNET permission?)")
            !isURLValid -> Logger.e("VGSCollect", "URL is not valid")
            !isValidData() -> return
            else -> func(storage.getStates())
        }
    }

    fun asyncSubmit(mainActivity:Activity
               , path:String
               , method:HTTPMethod = HTTPMethod.POST
               , headers:Map<String,String>? = null
    ) {
        appValidationCheck(mainActivity) { data ->
            doAsyncRequest(path, method, headers, data)
        }
    }

    private fun isValidData(): Boolean {
        var isValid = true
        storage.getStates().forEach {
            if(!it.isValid()) {
                val r = VGSResponse.ErrorResponse("is not a valid ${it.alias}", -1)
                onResponseListener?.onResponse(r)
                isValid = false
                return@forEach
            }
        }
        return isValid
    }


    private fun doInCurrentThread(
        path: String,
        method: HTTPMethod,
        headers: Map<String, String>?,
        data: MutableCollection<VGSFieldState>
    ) {
        client.call(path, method, headers, data.mapUsefulPayloads())
    }

    private fun doAsyncRequest(path: String,
                               method: HTTPMethod,
                               headers: Map<String, String>?,
                               data: MutableCollection<VGSFieldState>
    ) {
        val p = Payload(
            path,
            method,
            headers,
            data.mapUsefulPayloads()
        )

        val task = DoAsync(onResponseListener) {
            it?.run {
                client.call(this.path, this.method, this.headers, this.data)
            }?:VGSResponse.ErrorResponse("error:")  //fixme
        }.execute(p)

        tasks.add(task)
    }

    class DoAsync(listener: VgsCollectResponseListener?, val handler: (arg: Payload?) -> VGSResponse) : AsyncTask<Payload, Void, VGSResponse>() {
        var onResponseListener: WeakReference<VgsCollectResponseListener>? = WeakReference<VgsCollectResponseListener>(listener)
        override fun doInBackground(vararg arg: Payload?): VGSResponse? {
            val param = if(!arg.isNullOrEmpty()) {
                arg[0]
            } else {
                null
            }

            return handler(param)
        }

        override fun onPostExecute(result: VGSResponse?) {
            super.onPostExecute(result)
            if(onResponseListener == null) {
                Logger.i("VGSCollect", "VgsCollectResponseListener not set")
            } else {
                onResponseListener?.get()?.onResponse(result)
            }
        }
    }
}
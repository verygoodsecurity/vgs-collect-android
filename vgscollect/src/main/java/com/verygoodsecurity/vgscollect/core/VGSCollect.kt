package com.verygoodsecurity.vgscollect.core

import android.app.Activity
import android.content.pm.PackageManager
import android.webkit.URLUtil
import androidx.core.content.ContextCompat
import com.verygoodsecurity.vgscollect.core.api.ApiClient
import com.verygoodsecurity.vgscollect.core.api.URLConnectionClient
import com.verygoodsecurity.vgscollect.core.model.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.core.model.mapUsefulPayloads
import com.verygoodsecurity.vgscollect.core.storage.DefaultStorage
import com.verygoodsecurity.vgscollect.core.storage.IStateEmitter
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import com.verygoodsecurity.vgscollect.core.storage.VgsStore
import com.verygoodsecurity.vgscollect.util.Logger
import com.verygoodsecurity.vgscollect.widget.VGSEditText


class VGSCollect(id:String, environment: Environment = Environment.SANDBOX):AbstractVgsCollect(id, environment) {
    override lateinit var storage: VgsStore
    override lateinit var client: ApiClient
    private val emitter: IStateEmitter

    private val isURLValid:Boolean

    init {
        val store = DefaultStorage()
        storage = store
        emitter = store

        client = URLConnectionClient(baseURL)

        isURLValid = URLUtil.isValidUrl(baseURL)
    }

    override fun bindView(view: VGSEditText?) {
        if(view is VGSEditText) {
            view.inputField.stateListener = emitter.performSubscription()
        }
    }

    fun addOnFieldStateChangeListener(listener: OnFieldStateChangeListener?) {
        emitter.attachStateChangeListener(listener)
    }

    fun submit(mainActivity:Activity
               , path:String
               , method:HTTPMethod = HTTPMethod.POST
               , headers:Map<String,String>? = null
    ) {
        appValidationCheck(mainActivity) { data ->
            doRequest(path, method, headers, data)
//            client.call(path, method, headers, data.mapUsefulPayloads())
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
               , method:HTTPMethod
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
}
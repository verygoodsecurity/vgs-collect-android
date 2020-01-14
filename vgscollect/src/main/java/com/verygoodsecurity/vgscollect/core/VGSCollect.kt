package com.verygoodsecurity.vgscollect.core

import android.app.Activity
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.webkit.URLUtil
import androidx.core.content.ContextCompat
import com.verygoodsecurity.vgscollect.core.api.*
import com.verygoodsecurity.vgscollect.core.api.URLConnectionClient
import com.verygoodsecurity.vgscollect.core.storage.DependencyDispatcher
import com.verygoodsecurity.vgscollect.core.storage.Notifier
import com.verygoodsecurity.vgscollect.core.model.Payload
import com.verygoodsecurity.vgscollect.core.model.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.mapUsefulPayloads
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.core.model.state.mapToFieldState
import com.verygoodsecurity.vgscollect.core.storage.DefaultStorage
import com.verygoodsecurity.vgscollect.core.storage.IStateEmitter
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import com.verygoodsecurity.vgscollect.core.storage.VgsStore
import com.verygoodsecurity.vgscollect.util.Logger
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.widget.VGSEditText
import org.jetbrains.annotations.TestOnly

open class VGSCollect(id:String, environment: Environment = Environment.SANDBOX) {
    private var storage: VgsStore
    private val emitter: IStateEmitter
    private val dependencyDispatcher: DependencyDispatcher
    private var client: ApiClient

    var onResponseListener:VgsCollectResponseListener? = null
    @JvmName("addOnResponseListeners") set

    private val tasks = mutableListOf<AsyncTask<Payload, Void, VGSResponse>>()

    companion object {
        internal const val TAG = "VGSCollect"
    }

    internal val baseURL:String

    private val isURLValid:Boolean

    init {
        baseURL = if(id.isTennantIdValid()) {
            id.setupURL(environment.rawValue)
        } else {
            Logger.e(TAG, "tennantId is not valid")
            ""
        }
        isURLValid = URLUtil.isValidUrl(baseURL)

        dependencyDispatcher = Notifier()
        val store = DefaultStorage(dependencyDispatcher)

        storage = store
        emitter = store

        client = URLConnectionClient.newInstance(baseURL)
    }

    fun bindView(view: InputFieldView?) {
        if(view is VGSEditText) {
            view.addStateListener(emitter.performSubscription())
            dependencyDispatcher.addDependencyListener(view.getFieldType(), view.notifier)
        }
    }

    fun addOnFieldStateChangeListener(listener: OnFieldStateChangeListener?) {
        emitter.attachStateChangeListener(listener)
    }

    fun onDestroy() {
        tasks.forEach {
            it.cancel(true)
        }
        tasks.clear()
        storage.clear()
    }

    fun getAllStates(): List<FieldState> {
        return storage.getStates().map { it.mapToFieldState() }
    }

    fun submit(mainActivity:Activity
               , path:String
               , method:HTTPMethod = HTTPMethod.POST
    ) {
        appValidationCheck(mainActivity) { data ->
            val tempStore = client.getTemporaryStorage()
            val headers = tempStore.getCustomHeaders()
            val userData = tempStore.getCustomData()

            val dataBundledata = data.mapUsefulPayloads(userData)
            doRequest(path, method, headers, dataBundledata)
        }
    }

    fun asyncSubmit(mainActivity:Activity
                    , path:String
                    , method:HTTPMethod
    ) {
        appValidationCheck(mainActivity) { data ->
            val tempStore = client.getTemporaryStorage()
            val headers = tempStore.getCustomHeaders()
            val userData = tempStore.getCustomData()

            val dataBundledata = data.mapUsefulPayloads(userData)
            doAsyncRequest(path, method, headers, dataBundledata)
        }
    }

    private fun appValidationCheck(mainActivity: Activity, func: ( data: MutableCollection<VGSFieldState>) -> Unit) {
        when {
            ContextCompat.checkSelfPermission(mainActivity,android.Manifest.permission.INTERNET)
                    == PackageManager.PERMISSION_DENIED ->
                Logger.e(TAG, "Permission denied (missing INTERNET permission?)")
            !isURLValid -> Logger.e(TAG, "URL is not valid")
            !isValidData() -> return
            else -> func(storage.getStates())
        }
    }

    private fun isValidData(): Boolean {
        var isValid = true
        storage.getStates().forEach {
            if(!it.isValid) {
                val r = VGSResponse.ErrorResponse("FieldName is not a valid ${it.fieldName}", -1)
                onResponseListener?.onResponse(r)
                isValid = false
                return@forEach
            }
        }
        return isValid
    }

    protected fun doRequest(path: String,
                            method: HTTPMethod,
                            data: Map<String, String>?,
                            headers: Map<String, String>?
    ) {
        val r = client.call(path, method, headers, data)
        onResponseListener?.onResponse(r)
    }

    protected fun doAsyncRequest(path: String,
                                 method: HTTPMethod,
                                 data: Map<String, String>?,
                                 headers: Map<String, String>?
    ) {
        val p = Payload(path, method, data, headers)

        val task = doAsync(onResponseListener) {
            it?.run {
                client.call(this.path, this.method, this.headers, this.data)
            } ?: VGSResponse.ErrorResponse("error")  //fixme
        }

        tasks.add(task)

        task.execute(p)
    }

    fun setCustomHeaders(headers: Map<String, String>?) {
        client.getTemporaryStorage().setCustomHeaders(headers)
    }

    fun resetCustomHeaders() {
        client.getTemporaryStorage().resetCustomHeaders()
    }

    fun setCustomData(data: Map<String, String>?) {
        client.getTemporaryStorage().setCustomData(data)
    }

    fun resetCustomData() {
        client.getTemporaryStorage().resetCustomData()
    }

    @TestOnly
    internal fun setClient(c: ApiClient) {
        client = c
    }

    @TestOnly
    internal fun setStorage(store: VgsStore) {
        storage = store
    }

    @TestOnly
    internal fun doMainThreadRequest(path: String,
                                     method: HTTPMethod,
                                     headers: Map<String, String>?,
                                     data: MutableCollection<VGSFieldState>
    ) {
        val dataBundledata = data.mapUsefulPayloads()
        doRequest(path, method, headers, dataBundledata)
    }
}
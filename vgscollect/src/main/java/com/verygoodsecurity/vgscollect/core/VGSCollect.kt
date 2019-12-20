package com.verygoodsecurity.vgscollect.core

import android.app.Activity
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.webkit.URLUtil
import androidx.core.content.ContextCompat
import com.verygoodsecurity.vgscollect.core.storage.DependencyDispatcher
import com.verygoodsecurity.vgscollect.core.storage.Notifier
import com.verygoodsecurity.vgscollect.core.api.ApiClient
import com.verygoodsecurity.vgscollect.core.api.URLConnectionClient
import com.verygoodsecurity.vgscollect.core.api.doAsync
import com.verygoodsecurity.vgscollect.core.api.setupURL
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

    internal val baseURL:String = id.setupURL(environment.rawValue)

    private val isURLValid:Boolean

    init {
        dependencyDispatcher =
            Notifier()
        val store = DefaultStorage(dependencyDispatcher)

        storage = store
        emitter = store

        client = URLConnectionClient.newInstance(baseURL)

        isURLValid = URLUtil.isValidUrl(baseURL)
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
               , headers:Map<String,String>? = null
    ) {
        appValidationCheck(mainActivity) { data ->
            doRequest(path, method, headers, data)
        }
    }

    fun asyncSubmit(mainActivity:Activity
                    , path:String
                    , method:HTTPMethod
                    , headers:Map<String,String>?
    ) {

        appValidationCheck(mainActivity) { data ->
            doAsyncRequest(path, method, headers, data)
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
                            headers: Map<String, String>?,
                            data: MutableCollection<VGSFieldState>
    ) {
        val r = client.call(path, method, headers, data.mapUsefulPayloads())
        onResponseListener?.onResponse(r)
    }

    protected fun doAsyncRequest(path: String,
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

        val task = doAsync(onResponseListener) {
            it?.run {
                client.call(this.path, this.method, this.headers, this.data)
            } ?: VGSResponse.ErrorResponse("error")  //fixme
        }

        tasks.add(task)

        task.execute(p)
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
        doRequest(path, method, headers, data)
    }
}
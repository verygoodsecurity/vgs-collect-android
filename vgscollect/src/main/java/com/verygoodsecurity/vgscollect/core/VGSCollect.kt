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

/**
 * VGS Collect allows you to securely collect data from your users without having
 * to have that data pass through your systems.
 *
 * @param id Unique Vault id
 * @param environment Type of Vaults
 *
 * @version 1.0.1
 */
class VGSCollect(id:String, environment: Environment = Environment.SANDBOX) {
    private var storage: VgsStore
    private val emitter: IStateEmitter
    private val dependencyDispatcher: DependencyDispatcher
    private var client: ApiClient

    private val responseListeners = mutableListOf<VgsCollectResponseListener>()

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

    /**
     * Adds a listener to the list of those whose methods are called whenever the VGSCollect receive response from Server.
     *
     * @param onResponseListener listener which will be added.
     */
    fun addOnResponseListeners(onResponseListener:VgsCollectResponseListener?) {
        onResponseListener?.let {
            responseListeners.add(it)
        }
    }

    /**
     * Allows VGS secure fields to interact with @VGSCollect.
     *
     * @param view base class for VGS secure fields.
     */
    fun bindView(view: InputFieldView?) {
        if(view is VGSEditText) {
            view.addStateListener(emitter.performSubscription())
            dependencyDispatcher.addDependencyListener(view.getFieldType(), view.notifier)
        }
    }

    /**
     * This method adds a listener whose methods are called whenever VGS secure fields state changes.
     *
     * @param fieldStateListener listener which will receive changes updates
     */
    fun addOnFieldStateChangeListener(fieldStateListener : OnFieldStateChangeListener?) {
        emitter.attachStateChangeListener(fieldStateListener)
    }

    /**
     * Clear all information collected before by VGSCollect.
     */
    fun onDestroy() {
        tasks.forEach {
            it.cancel(true)
        }
        tasks.clear()
        storage.clear()
    }

    /**
     * Returns the states of all fields bonded before to VGSCollect.
     *
     * @return the  list with all states.
     */
    fun getAllStates(): List<FieldState> {
        return storage.getStates().map { it.mapToFieldState() }
    }

    /**
     * This method executes and send data on VGS Server. It could be useful if you want to handle
     * multithreading by yourself.
     * Do not use this method on the UI thread as this may crash.
     *
     * @param mainActivity current activity
     * @param path path for a request
     * @param method HTTP method
     */
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

    /**
     * This method executes and send data on VGS Server.
     *
     * @param mainActivity current activity
     * @param path path for a request
     * @param method HTTP method
     */
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
                responseListeners.forEach { it.onResponse(r) }
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
        responseListeners.forEach { it.onResponse(r) }
    }

    protected fun doAsyncRequest(path: String,
                                 method: HTTPMethod,
                                 data: Map<String, String>?,
                                 headers: Map<String, String>?
    ) {
        val p = Payload(path, method, data, headers)

        val task = doAsync(responseListeners) {
            it?.run {
                client.call(this.path, this.method, this.headers, this.data)
            } ?: VGSResponse.ErrorResponse("error")  //fixme
        }

        tasks.add(task)

        task.execute(p)
    }

    /**
     * It collect headers which will be send to server. Headers are stored until
     * resetCustomHeaders method will be called
     *
     * @param headers The headers to save for request.
     */
    fun setCustomHeaders(headers: Map<String, String>?) {
        client.getTemporaryStorage().setCustomHeaders(headers)
    }

    /**
     * Reset all headers which added before.
     */
    fun resetCustomHeaders() {
        client.getTemporaryStorage().resetCustomHeaders()
    }

    /**
     * It collect custom data which will be send to server. User's custom data are stored until
     * resetCustomData method will be called.
     *
     * @param data The Map to save for request.
     */
    fun setCustomData(data: Map<String, String>?) {
        client.getTemporaryStorage().setCustomData(data)
    }

    /**
     * Reset all custom data which added before.
     */
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
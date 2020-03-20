package com.verygoodsecurity.vgscollect.core

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import androidx.core.content.ContextCompat
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.app.BaseTransmitActivity
import com.verygoodsecurity.vgscollect.core.api.*
import com.verygoodsecurity.vgscollect.core.model.Payload
import com.verygoodsecurity.vgscollect.core.model.VGSHashMapWrapper
import com.verygoodsecurity.vgscollect.core.model.VGSRequest
import com.verygoodsecurity.vgscollect.core.model.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.core.model.state.mapToFieldState
import com.verygoodsecurity.vgscollect.core.storage.*
import com.verygoodsecurity.vgscollect.core.storage.content.file.FFFF
import com.verygoodsecurity.vgscollect.core.storage.content.file.FileStorage
import com.verygoodsecurity.vgscollect.core.storage.content.file.VGSContentProvider
import com.verygoodsecurity.vgscollect.core.storage.content.file.VGSContentProviderImpl
import com.verygoodsecurity.vgscollect.core.storage.external.DependencyReceiver
import com.verygoodsecurity.vgscollect.core.storage.external.ExternalDependencyDispatcher
import com.verygoodsecurity.vgscollect.util.Logger
import com.verygoodsecurity.vgscollect.util.mapUsefulPayloads
import com.verygoodsecurity.vgscollect.view.AccessibilityStatePreparer
import com.verygoodsecurity.vgscollect.view.InputFieldView
import org.jetbrains.annotations.TestOnly


/**
 * VGS Collect allows you to securely collect data from your users without having
 * to have that data pass through your systems.
 *
 * @param context Activity context
 * @param id Unique Vault id
 * @param environment Type of Vaults
 *
 * @version 1.0.0
 */
class VGSCollect(
    private val context: Context,
    id: String,
    environment: Environment = Environment.SANDBOX
) {
    private var storage: VgsStore<VGSFieldState>
    private val emitter: IStateEmitter
    private val dependencyDispatcher: DependencyDispatcher
    private val externalDependencyDispatcher: ExternalDependencyDispatcher
    private var client: ApiClient
    private val fileProvider: VGSContentProvider
    private val fileStorage: FileStorage

    private val responseListeners = mutableListOf<VgsCollectResponseListener>()

    private val tasks = mutableListOf<AsyncTask<Payload, Void, VGSResponse>>()

    internal val baseURL:String = id.setupURL(environment.rawValue)

    private val isURLValid:Boolean

    init {
        isURLValid = baseURL.isURLValid()

        with(
            VGSContentProviderImpl(
                context
            )
        ) {
            fileProvider = this
            fileStorage = this
        }

        dependencyDispatcher = Notifier()
        externalDependencyDispatcher = DependencyReceiver()

        with(DefaultStorage()) {
            attachFieldDependencyObserver(dependencyDispatcher)

            storage = this
            emitter = this
        }

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
        if(view is AccessibilityStatePreparer) {
            dependencyDispatcher.addDependencyListener(view.getFieldType(), view.getDependencyListener())
            externalDependencyDispatcher.addDependencyListener(view.getFieldName(), view.getDependencyListener())
        }
        view?.addStateListener(emitter.performSubscription())
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
        return storage.getItems().map { it.mapToFieldState() }
    }

    /**
     * This method executes and send data on VGS Server. It could be useful if you want to handle
     * multithreading by yourself.
     * Do not use this method on the UI thread as this may crash.
     *
     * @param path path for a request
     * @param method HTTP method
     */
    fun submit(path:String
               , method:HTTPMethod = HTTPMethod.POST
    ) {
        appValidationCheck { data ->
            val tempStore = client.getTemporaryStorage()
            val headers = tempStore.getCustomHeaders()
            val userData = tempStore.getCustomData()

            val dataBundledata = data.mapUsefulPayloads(userData)
            doRequest(path, method, headers, dataBundledata)
        }
    }


    /**
     * This method executes and send data on VGS Server. It could be useful if you want to handle
     * multithreading by yourself.
     * Do not use this method on the UI thread as this may crash.
     *
     * @param request data class with attributes for submit
     */
    fun submit(request: VGSRequest) {
        appValidationCheck { data ->
            val tempStore = client.getTemporaryStorage()
            val headers = tempStore.getCustomHeaders()
            headers.putAll(request.customHeader)
            val userData = tempStore.getCustomData()
            userData.putAll(request.customData)

            val dataBundleData = data.mapUsefulPayloads(userData)
            doAsyncRequest(request.path, request.method, headers, dataBundleData)
        }
    }

    /**
     * This method executes and send data on VGS Server.
     *
     * @param path path for a request
     * @param method HTTP method
     */
    fun asyncSubmit(path:String
                    , method:HTTPMethod
    ) {
        appValidationCheck { data ->
            val tempStore = client.getTemporaryStorage()
            val headers = tempStore.getCustomHeaders()
            val userData = tempStore.getCustomData()
            val dataBundledata = data.mapUsefulPayloads(userData)
            doAsyncRequest(path, method, headers, dataBundledata)
        }
    }

    /**
     * This method executes and send data on VGS Server.
     *
     * @param request data class with attributes for submit
     */
    fun asyncSubmit(request: VGSRequest) {
        appValidationCheck { data ->
            val tempStore = client.getTemporaryStorage()
            val headers = tempStore.getCustomHeaders()
            headers.putAll(request.customHeader)
            val userData = tempStore.getCustomData()
            userData.putAll(request.customData)

            val dataBundleData = data.mapUsefulPayloads(userData)
            doAsyncRequest(request.path, request.method, headers, dataBundleData)
        }
    }

    private fun appValidationCheck(func: ( data: MutableCollection<VGSFieldState>) -> Unit) {
        when {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.INTERNET)
                    == PackageManager.PERMISSION_DENIED ->
                Logger.e(context, VGSCollect::class.java, R.string.error_internet_permission)
            !isURLValid -> Logger.e(context, VGSCollect::class.java, R.string.error_url_validation)
            !isValidData(context) -> return
            else -> func(storage.getItems())
        }
    }

    private fun isValidData(context: Context): Boolean {
        var isValid = true
        storage.getItems().forEach {
            if(!it.isValid) {
                val message = String.format(
                    context.getString(R.string.error_field_validation),
                    it.fieldName
                )
                val r = VGSResponse.ErrorResponse(message, -1)
                responseListeners.forEach { it.onResponse(r) }
                isValid = false
                return@forEach
            }
        }
        return isValid
    }

    protected fun doRequest(path: String,
                            method: HTTPMethod,
                            headers: Map<String, String>?,
                            data: Map<String, Any>?
    ) {
        val r = client.call(path, method, headers, data)
        responseListeners.forEach { it.onResponse(r) }
    }

    protected fun doAsyncRequest(path: String,
                                 method: HTTPMethod,
                                 headers: Map<String, String>?,
                                 data: Map<String, Any>?
    ) {
        val p = Payload(path, method, headers, data)

        val task = doAsync(responseListeners) {
            it?.run {
                client.call(this.path, this.method, this.headers, this.data)
            } ?: VGSResponse.ErrorResponse()
        }

        tasks.add(task)

        task.execute(p)
    }

    fun test() {
        val uri = Uri.parse(fileStorage.getItems().toList().get(0))
        val resolver: ContentResolver = context.getContentResolver()
        val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
        resolver.takePersistableUriPermission(uri, takeFlags)
        Log.e("test","NEW FILE ${fileStorage.getItems().toList().get(0)}")
    }

    /**
     * Called when an activity you launched exits,
     * giving you the requestCode you started it with, the resultCode is returned,
     * and any additional data for VGSCollect.
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK) {
            val map: VGSHashMapWrapper<String, Any?>? = data?.extras?.getParcelable(
                BaseTransmitActivity.RESULT_DATA
            )

            if(requestCode == VGSContentProviderImpl.REQUEST_CODE) {
                fileStorage.getFileCipher().retrieveActivityResult(map)?.let { uri ->
                    fileStorage.addItem(-1, uri)
                }
            } else {
                map?.run {
                    externalDependencyDispatcher.dispatch(mapOf())
                }
            }
        }
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
    fun setCustomData(data: Map<String, Any>?) {
        client.getTemporaryStorage().setCustomData(data)
    }

    /**
     * Reset all custom data which added before.
     */
    fun resetCustomData() {
        client.getTemporaryStorage().resetCustomData()
    }

    fun getFileProvider(): VGSContentProvider {
        return fileProvider
    }

    @TestOnly
    internal fun setClient(c: ApiClient) {
        client = c
    }

    @TestOnly
    internal fun setStorage(store: VgsStore<VGSFieldState>) {
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


//    val intentFilter = IntentFilter("some123code")
//    val r = AlarmReceiver()
//    fun onResume() {
//        Log.e("test", "VGS onResume")
//        (context as Activity).registerReceiver(r, intentFilter)
//    }
//
//    fun onPause() {
//        Log.e("test", "VGS onPause")
//        (context as Activity).unregisterReceiver(r)
//    }
//
//    class AlarmReceiver : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            Log.e("test", "BroadcastReceiver done")
//        }
//    }
}
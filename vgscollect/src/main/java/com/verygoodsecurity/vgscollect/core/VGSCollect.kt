package com.verygoodsecurity.vgscollect.core

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import androidx.annotation.VisibleForTesting
import androidx.core.content.ContextCompat
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.app.BaseTransmitActivity
import com.verygoodsecurity.vgscollect.core.api.*
import com.verygoodsecurity.vgscollect.core.model.Payload
import com.verygoodsecurity.vgscollect.core.model.VGSHashMapWrapper
import com.verygoodsecurity.vgscollect.core.model.VGSRequest
import com.verygoodsecurity.vgscollect.core.model.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.model.state.mapToFieldState
import com.verygoodsecurity.vgscollect.core.storage.*
import com.verygoodsecurity.vgscollect.core.storage.content.file.StorageErrorListener
import com.verygoodsecurity.vgscollect.core.storage.content.file.VGSFileProvider
import com.verygoodsecurity.vgscollect.core.storage.content.file.TemporaryFileStorage
import com.verygoodsecurity.vgscollect.core.storage.external.DependencyReceiver
import com.verygoodsecurity.vgscollect.core.storage.external.ExternalDependencyDispatcher
import com.verygoodsecurity.vgscollect.util.Logger
import com.verygoodsecurity.vgscollect.util.mapUsefulPayloads
import com.verygoodsecurity.vgscollect.view.AccessibilityStatePreparer
import com.verygoodsecurity.vgscollect.view.InputFieldView


/**
 * VGS Collect allows you to securely collect data from your users without having
 * to have that data pass through your systems.
 * Entry-point to the Stripe SDK.
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
) : StorageErrorListener {

    private val externalDependencyDispatcher: ExternalDependencyDispatcher

    private var client: ApiClient

    private var storage:InternalStorage

    private val responseListeners = mutableListOf<VgsCollectResponseListener>()

    private var currentTask:AsyncTask<Payload, Void, VGSResponse>? = null

    private val baseURL:String = id.setupURL(environment.rawValue)

    private val isURLValid:Boolean

    init {
        isURLValid = baseURL.isURLValid()

        externalDependencyDispatcher = DependencyReceiver()

        storage = InternalStorage(context, this)

        client = OkHttpClient.newInstance(context, baseURL)
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
            externalDependencyDispatcher.addDependencyListener(view.getFieldName(), view.getDependencyListener())
        }
        storage.performSubscription(view)
    }

    /**
     * This method adds a listener whose methods are called whenever VGS secure fields state changes.
     *
     * @param fieldStateListener listener which will receive changes updates
     */
    fun addOnFieldStateChangeListener(fieldStateListener : OnFieldStateChangeListener?) {
        storage.attachStateChangeListener(fieldStateListener)
    }

    /**
     * Clear all information collected before by VGSCollect.
     */
    fun onDestroy() {
        currentTask?.cancel(true)
        responseListeners.clear()
        storage.clear()
    }

    /**
     * Returns the states of all fields bonded before to VGSCollect.
     *
     * @return the list with all states.
     */
    fun getAllStates(): List<FieldState> {
        return storage.getFieldsStates().map { it.mapToFieldState() }
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
        val request = VGSRequest.VGSRequestBuilder()
            .setPath(path)
            .setMethod(method)
            .build()

        if(checkInternetPermission() && isUrlValid() && validateFields() && validateFiles()) {
            doRequest(request)
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
        if(isUrlValid() && checkInternetPermission()) {
            if(!request.fieldsIgnore && !validateFields()) {
                return
            }
            if(!request.fileIgnore&& !validateFiles()) {
                return
            }
            doRequest(request)
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
        val request = VGSRequest.VGSRequestBuilder()
            .setPath(path)
            .setMethod(method)
            .build()

        if(checkInternetPermission() && isUrlValid() && validateFields() && validateFiles()) {
            doAsyncRequest(request)
        }
    }

    /**
     * This method executes and send data on VGS Server.
     *
     * @param request data class with attributes for submit
     */
    fun asyncSubmit(request: VGSRequest) {
        if(isUrlValid() && checkInternetPermission()) {
            if(!request.fieldsIgnore && !validateFields()) {
                return
            }
            if(!request.fileIgnore&& !validateFiles()) {
                return
            }
            doAsyncRequest(request)
        }
    }

    private fun checkInternetPermission():Boolean {
        return with(ContextCompat.checkSelfPermission(context, android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_DENIED) {
            if (this.not()) {
                notifyErrorResponse(R.string.error_internet_permission)
            }
            this
        }
    }

    private fun isUrlValid():Boolean {
        return with(isURLValid) {
            if (this.not()) {
                notifyErrorResponse(R.string.error_url_validation)
            }
            this
        }
    }

    override fun onStorageError(messageResId: Int) {
        notifyErrorResponse(messageResId)
    }

    private fun notifyErrorResponse(messageResId:Int) {
        val message = context.getString(messageResId)
        responseListeners.forEach {
            it.onResponse(VGSResponse.ErrorResponse(message, -1))
        }
        Logger.e(VGSCollect::class.java, message)
    }

    private fun validateFiles():Boolean {
        var isValid = true

        storage.getAttachedFiles().forEach {
            if(it.size > storage.getFileSizeLimit()) {
                val message = String.format(
                    context.getString(R.string.error_file_size_validation),
                    it.name
                )
                val r = VGSResponse.ErrorResponse(message, -1)
                responseListeners.forEach { it.onResponse(r) }

                isValid = false
                return@forEach
            }
        }

        return isValid
    }

    private fun validateFields():Boolean {
        var isValid = true

        storage.getFieldsStorage().getItems().forEach {
            if(it.isValid.not()) {
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

    private fun doRequest(
        request: VGSRequest
    ) {
        val requestBodyMap = request.customData.run {
            val map = HashMap<String, Any>()
            map.putAll(this)
            map
        }

        val data = storage.getAssociatedList(request.fieldsIgnore, request.fileIgnore).mapUsefulPayloads(requestBodyMap)
        val r = client.call(request.path, request.method, request.customHeader, data)
        responseListeners.forEach { it.onResponse(r) }
    }

    private fun doAsyncRequest(
        request: VGSRequest
    ) {
        if(currentTask?.isCancelled == false) {
            currentTask?.cancel(true)
        }
        currentTask = doAsync(responseListeners) {
            it?.run {
                val requestBodyMap = data?.run {
                    val map = HashMap<String, Any>()
                    map.putAll(this)
                    map
                }

                val data = storage.getAssociatedList(request.fieldsIgnore, request.fileIgnore).mapUsefulPayloads(requestBodyMap)
                val r = client.call(this.path, this.method, this.headers, data)
                r
            } ?: VGSResponse.ErrorResponse()
        }

        val p = Payload(request.path, request.method, request.customHeader, request.customData)
        currentTask!!.execute(p)
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

            if(requestCode == TemporaryFileStorage.REQUEST_CODE) {
                map?.run {
                    storage.getFileStorage().dispatch(mapOf())
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

    /**
     * Return instance for managing attached files to request.
     *
     * @return [VGSFileProvider] instance
     */
    fun getFileProvider(): VGSFileProvider {
        return storage.getFileProvider()
    }

    @VisibleForTesting
    internal fun getResponseListeners(): Collection<VgsCollectResponseListener> {
        return responseListeners
    }

    @VisibleForTesting
    internal fun setStorage(store: InternalStorage) {
        storage = store
    }

    @VisibleForTesting
    internal fun setClient(c: ApiClient) {
        client = c
    }
}
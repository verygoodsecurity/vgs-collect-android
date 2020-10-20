package com.verygoodsecurity.vgscollect.core

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import androidx.annotation.IntRange
import androidx.annotation.VisibleForTesting
import androidx.core.content.ContextCompat
import com.verygoodsecurity.vgscollect.app.BaseTransmitActivity
import com.verygoodsecurity.vgscollect.core.api.*
import com.verygoodsecurity.vgscollect.core.api.analityc.CollectActionTracker
import com.verygoodsecurity.vgscollect.core.api.analityc.AnalyticTracker
import com.verygoodsecurity.vgscollect.core.api.analityc.action.*
import com.verygoodsecurity.vgscollect.core.model.*
import com.verygoodsecurity.vgscollect.core.model.VGSHashMapWrapper
import com.verygoodsecurity.vgscollect.core.model.network.VGSError
import com.verygoodsecurity.vgscollect.core.model.network.VGSRequest
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.model.state.mapToFieldState
import com.verygoodsecurity.vgscollect.core.storage.*
import com.verygoodsecurity.vgscollect.core.storage.content.file.StorageErrorListener
import com.verygoodsecurity.vgscollect.core.storage.content.file.VGSFileProvider
import com.verygoodsecurity.vgscollect.core.storage.content.file.TemporaryFileStorage
import com.verygoodsecurity.vgscollect.core.storage.external.DependencyReceiver
import com.verygoodsecurity.vgscollect.core.storage.external.ExternalDependencyDispatcher
import com.verygoodsecurity.vgscollect.util.*
import com.verygoodsecurity.vgscollect.util.Logger
import com.verygoodsecurity.vgscollect.util.mapUsefulPayloads
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.card.getAnalyticName
import java.security.MessageDigest
import java.util.*
import kotlin.collections.HashMap

/**
 * VGS Collect allows you to securely collect data and files from your users without having
 * to have them pass through your systems.
 * Entry-point to the Collect SDK.
 *
 * @since 1.0.1
 */
class VGSCollect {

    private val externalDependencyDispatcher: ExternalDependencyDispatcher

    private val tracker:AnalyticTracker

    private var client: ApiClient

    private var storage:InternalStorage
    private var storageErrorListener:StorageErrorListener

    private val responseListeners = mutableListOf<VgsCollectResponseListener>()
    private val analyticListener = object :VgsCollectResponseListener {
        override fun onResponse(response: VGSResponse?) {
            when(response) {
                is VGSResponse.ErrorResponse -> responseEvent(response.code, response.localizeMessage)
                is VGSResponse.SuccessResponse -> responseEvent(response.code)
            }
        }
    }

    private var currentTask:AsyncTask<Payload, Void, VGSResponse>? = null

    private val baseURL:String
    private val context: Context

    private val isURLValid:Boolean

    /**
     * @param id Unique Vault id
     * @param environment Type of Vault
     */
    constructor(
        context: Context,
        id: String,
        environment: Environment = Environment.SANDBOX
    ): this(context, id, environment.rawValue, null)

    /**
     * @param id Unique Vault id
     * @param environment Type of Vault
     */
    constructor(
        context: Context,
        id: String,
        environment: String
    ) : this(context, id, environment, null)

    /**
     * @param id Unique Vault id
     * @param environment Type of Vault
     * @param port Optional endpoint port number (min = [PORT_MIN_VALUE], max = [PORT_MAX_VALUE])
     */
    constructor(
        context: Context,
        id: String,
        environment: String,
        @IntRange(from = PORT_MIN_VALUE, to = PORT_MAX_VALUE) port: Int?
    ) {
        this.context = context
        tracker = CollectActionTracker(context, id, environment, UUID.randomUUID().toString())
        baseURL = id.setupURL(environment, port)
        isURLValid = baseURL.isURLValid()
        client = OkHttpClient.newInstance(context, baseURL)
        storage = InternalStorage(context, storageErrorListener)
    }

    init {
        externalDependencyDispatcher = DependencyReceiver()

        storageErrorListener = object : StorageErrorListener {
            override fun onStorageError(error: VGSError) {
                notifyErrorResponse(error)
            }
        }
        addOnResponseListeners(analyticListener)
    }

    /**
     * Adds a listener to the list of those whose methods are called whenever the VGSCollect receive response from Server.
     *
     * @param onResponseListener Interface definition for a receiving callback.
     */
    fun addOnResponseListeners(onResponseListener:VgsCollectResponseListener?) {
        onResponseListener?.let {
            if(!responseListeners.contains(it)) responseListeners.add(it)
        }
    }

    /**
     * Clear all response listeners attached before.
     */
    fun clearResponseListeners() {
        responseListeners.clear()
        responseListeners.add(analyticListener)
    }

    /**
     * Clear specific listener attached before.
     *
     * @param onResponseListener Interface definition for a receiving callback.
     */
    fun removeOnResponseListener(onResponseListener:VgsCollectResponseListener) {
        if(responseListeners.contains(onResponseListener)) responseListeners.remove(onResponseListener)
    }

    /**
     * Allows VGS secure fields to interact with [VGSCollect] and collect data from this source.
     *
     * @param view base class for VGS secure fields.
     */
    fun bindView(view: InputFieldView?) {
        view?.statePreparer?.let {
            externalDependencyDispatcher.addDependencyListener(view.getFieldName(), it.getDependencyListener())

            it.setAnalyticTracker(tracker)
        }

        storage.performSubscription(view)

        initField(view)
    }

    /**
     * This method adds a listener whose methods are called whenever VGS secure fields state changes.
     *
     * @param fieldStateListener listener which will notify about changes inside input fields.
     */
    fun addOnFieldStateChangeListener(fieldStateListener : OnFieldStateChangeListener?) {
        storage.attachStateChangeListener(fieldStateListener)
    }

    /**
     * Clear all information collected before by VGSCollect.
     * Preferably call it inside onDestroy system's callback.
     */
    fun onDestroy() {
        currentTask?.cancel(true)
        responseListeners.clear()
        storage.clear()
    }

    /**
     * Returns the states of all fields bound before to VGSCollect.
     *
     * @return the list of all input fields states, that were bound before.
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
            submitEvent(true, !request.fileIgnore, !request.fieldsIgnore,
                request.customHeader.isNotEmpty(), request.customData.isNotEmpty()
            )
        }
    }


    /**
     * This method executes and send data on VGS Server. It could be useful if you want to handle
     * multithreading by yourself.
     * Do not use this method on the UI thread as this may crash.
     *
     * @param request data class with attributes for submit.
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
            submitEvent(true, !request.fileIgnore, !request.fieldsIgnore,
                request.customHeader.isNotEmpty(), request.customData.isNotEmpty()
            )
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

            submitEvent(true, !request.fileIgnore, !request.fieldsIgnore,
                request.customHeader.isNotEmpty(), request.customData.isNotEmpty()
            )
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
            if(!request.fileIgnore && !validateFiles()) {
                return
            }
            doAsyncRequest(request)

            submitEvent(true, !request.fileIgnore, !request.fieldsIgnore,
                request.customHeader.isNotEmpty(), request.customData.isNotEmpty()
            )
        }
    }

    private fun checkInternetPermission():Boolean {
        return with(ContextCompat.checkSelfPermission(context, android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_DENIED) {
            if (this.not()) {
                notifyErrorResponse(VGSError.NO_INTERNET_PERMISSIONS)
            }
            this
        }
    }

    private fun isUrlValid():Boolean {
        return with(isURLValid) {
            if (this.not()) {
                notifyErrorResponse(VGSError.URL_NOT_VALID)
            }
            this
        }
    }

    private fun validateFiles():Boolean {
        var isValid = true

        storage.getAttachedFiles().forEach {
            if(it.size > storage.getFileSizeLimit()) {
                notifyErrorResponse(VGSError.FILE_SIZE_OVER_LIMIT, it.name)

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
                notifyErrorResponse(VGSError.INPUT_DATA_NOT_VALID, it.fieldName)
                isValid = false
                return isValid
            }
        }
        return isValid
    }

    private fun notifyErrorResponse(error: VGSError, vararg params:String?) {
        val message = if(params.isEmpty()) {
            context.getString(error.messageResId)
        } else {
            String.format(
                context.getString(error.messageResId),
                *params
            )
        }
        responseListeners.forEach {
            it.onResponse(VGSResponse.ErrorResponse(message, error.code))
        }
        Logger.e(VGSCollect::class.java, message)
        submitEvent(false, code = error.code)
    }

    private fun doRequest(
        request: VGSRequest
    ) {
        val data = retrieveData(request.customData, request.fieldsIgnore, request.fileIgnore)
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
                val data = retrieveData(request.customData, request.fieldsIgnore, request.fileIgnore)
                val r = client.call(this.path, this.method, this.headers, data)
                r
            } ?: VGSResponse.ErrorResponse()
        }

        val p = Payload(request.path, request.method, request.customHeader, request.customData)
        currentTask!!.execute(p)
    }

    private fun retrieveData(customData: HashMap<String, Any> = HashMap(),
                             fieldsIgnore:Boolean = false,
                             fileIgnore:Boolean = false
    ): Map<String, Any>? {
        val requestBodyMap = customData.run {
            val map = HashMap<String, Any>()
            val customDataTmp = client.getTemporaryStorage().getCustomData()
            val newDynamicData = this.mapToMap()
            val newStaticData = customDataTmp.mapToMap()
            val mergedMap = newStaticData.deepMerge(newDynamicData)

            map.putAll(mergedMap)
            map
        }

        return storage.getAssociatedList(fieldsIgnore, fileIgnore)
            .mapUsefulPayloads(requestBodyMap)
    }

    /**
     * Called when an activity you launched exits,
     * giving you the requestCode you started it with, the resultCode is returned,
     * and any additional data for VGSCollect.
     * Preferably call it inside onActivityResult system's callback.
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
        mapAnalyticEvent(data)

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

    private fun mapAnalyticEvent(data: Intent?) {
        data?.let {
            val map: VGSHashMapWrapper<String, Any?> = it.extras?.getParcelable(
                BaseTransmitActivity.RESULT_DATA
            )?: VGSHashMapWrapper()

            when(map.get(BaseTransmitActivity.RESULT_TYPE)) {
                BaseTransmitActivity.SCAN -> scanEvent(
                    map.get(BaseTransmitActivity.RESULT_STATUS).toString(),
                    map.get(BaseTransmitActivity.RESULT_NAME).toString(),
                    map.get(BaseTransmitActivity.RESULT_ID) as? String
                )
                BaseTransmitActivity.ATTACH -> attachFileEvent(
                    map.get(BaseTransmitActivity.RESULT_STATUS).toString()
                )
            }

        }
    }

    /**
     * It collects headers that will be sent to the server.
     * This is static headers that are stored and attach for all requests until @resetCustomHeaders method will be called.
     *
     * @param headers The headers to save for request.
     */
    fun setCustomHeaders(headers: Map<String, String>?) {
        client.getTemporaryStorage().setCustomHeaders(headers)
    }

    /**
     * Reset all static headers which added before.
     * This method has no impact on all custom data that were added with [VGSRequest]
     */
    fun resetCustomHeaders() {
        client.getTemporaryStorage().resetCustomHeaders()
    }

    /**
     * It collect custom data which will be send to server.
     * This is static custom data that are stored and attach for all requests until resetCustomData method will be called.
     *
     * @param data The Map to save for request.
     */
    fun setCustomData(data: Map<String, Any>?) {
        client.getTemporaryStorage().setCustomData(data)
    }

    /**
     * Reset all static custom data which added before.
     * This method has no impact on all custom data that were added with [VGSRequest]
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






    private fun initField(view: InputFieldView?) {
        val m = view?.getFieldType()?.getAnalyticName()?.run {
            with(mutableMapOf<String, String>()) {
                put("field", this@run)
                this
            }
        }?: mutableMapOf()

        tracker.logEvent(
            InitAction(m)
        )
    }

    private fun scanEvent(status:String, type:String, id:String?) {
        val m = with(mutableMapOf<String, String>()) {
            put("status", status)
            put("scannerType", type)
            if(!id.isNullOrEmpty()) put("scanId", id.toString())

            this
        }
        tracker.logEvent(
            ScanAction(m)
        )
    }

    private fun String.toMD5(): String {
        val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun calculateCheckSum(): String {
        return retrieveData()?.mapToJSON().toString().toMD5()
    }

    private fun submitEvent(
        isSuccess: Boolean,
        hasFiles: Boolean = false,
        hasFields: Boolean = false,
        hasCustomHeader: Boolean = false,
        hasCustomData: Boolean = false,
        code:Int = 200
    ) {
        if(code >= 1000 || code == 200) {
            val m = with(mutableMapOf<String, Any>()) {
                if (isSuccess) put("status", "Ok") else put("status", "Failed")

                put("statusCode", code)

                put("checkSum", calculateCheckSum())

                val arr = with(mutableListOf<String>()) {
                    if (hasFiles) add("file")
                    if (hasFields) add("fields")
                    if (hasCustomHeader || client.getTemporaryStorage().getCustomHeaders().isNotEmpty()) add("customHeaders")
                    if (hasCustomData || client.getTemporaryStorage().getCustomData().isNotEmpty()) add("customData")
                    this
                }

                put("content", arr)

                this
            }

            tracker.logEvent(
                SubmitAction(m)
            )
        }
    }

    private fun responseEvent(code: Int, message:String? = null) {
        if(code in 200..999) {
            val m = with(mutableMapOf<String, Any>()) {
                put("statusCode", code)
                put("status", BaseTransmitActivity.Status.SUCCESS.raw)
                put("checkSum", calculateCheckSum())
                if(!message.isNullOrEmpty()) put("error", message)

                this
            }
            tracker.logEvent(
                ResponseAction(m)
            )
        }
    }

    private fun attachFileEvent(status:String) {//MIME, success
        val m = with(mutableMapOf<String, Any>()) {
            put("status", status)

            this
        }
        tracker.logEvent(
            AttachFileAction(m)
        )
    }
}
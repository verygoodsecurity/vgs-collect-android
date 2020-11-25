package com.verygoodsecurity.vgscollect.core

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.annotation.VisibleForTesting
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.app.BaseTransmitActivity
import com.verygoodsecurity.vgscollect.core.api.*
import com.verygoodsecurity.vgscollect.core.api.analityc.CollectActionTracker
import com.verygoodsecurity.vgscollect.core.api.analityc.AnalyticTracker
import com.verygoodsecurity.vgscollect.core.api.analityc.action.*
import com.verygoodsecurity.vgscollect.core.api.analityc.utils.toAnalyticStatus
import com.verygoodsecurity.vgscollect.core.api.client.ApiClient
import com.verygoodsecurity.vgscollect.core.api.client.extension.isHttpStatusCode
import com.verygoodsecurity.vgscollect.core.model.VGSHashMapWrapper
import com.verygoodsecurity.vgscollect.core.model.network.*
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
import com.verygoodsecurity.vgscollect.util.extension.concatWithDash
import com.verygoodsecurity.vgscollect.util.extension.hasAccessNetworkStatePermission
import com.verygoodsecurity.vgscollect.util.extension.hasInternetPermission
import com.verygoodsecurity.vgscollect.util.extension.isConnectionAvailable
import com.verygoodsecurity.vgscollect.util.mapUsefulPayloads
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.card.getAnalyticName
import java.util.*
import kotlin.collections.HashMap

/**
 * VGS Collect allows you to securely collect data and files from your users without having
 * to have them pass through your systems.
 * Entry-point to the Collect SDK.
 *
 * @since 1.0.0
 */
class VGSCollect {

    private val externalDependencyDispatcher: ExternalDependencyDispatcher

    private val tracker: AnalyticTracker

    private lateinit var client: ApiClient
    private val mainHandler: Handler = Handler(Looper.getMainLooper())

    private lateinit var storage: InternalStorage
    private val storageErrorListener: StorageErrorListener = object : StorageErrorListener {
        override fun onStorageError(error: VGSError) {
            VGSError.INPUT_DATA_NOT_VALID.toVGSResponse(context).also { r ->
                notifyAllListeners(r)
                Logger.e(VGSCollect::class.java, r.localizeMessage)
                submitEvent(false, code = r.errorCode)
            }
        }
    }

    private val responseListeners = mutableListOf<VgsCollectResponseListener>()
    private val analyticListener = object : VgsCollectResponseListener {
        override fun onResponse(response: VGSResponse?) {
            when (response) {
                is VGSResponse.ErrorResponse -> responseEvent(
                    response.code,
                    response.localizeMessage
                )
                is VGSResponse.SuccessResponse -> responseEvent(response.code)
            }
        }
    }

    private var baseURL: String
    private val context: Context

    constructor(
        /** Activity context */
        context: Context,

        /** Unique Vault id */
        id: String,

        /** Type of Vault */
        environment: Environment = Environment.SANDBOX
    ) {
        this.context = context

        tracker = CollectActionTracker(
            id,
            environment.rawValue,
            UUID.randomUUID().toString()
        )

        baseURL = id.setupURL(environment.rawValue)
        initializeCollect()
    }

    constructor(
        /** Activity context */
        context: Context,

        /** Unique Vault id */
        id: String,

        /** Type of Environment */
        environmentType: String,

        /** Region identifier */
        suffix: String
    ) : this(context, id, environmentType concatWithDash suffix)

    constructor(
        /** Activity context */
        context: Context,

        /** Unique Vault id */
        id: String,

        /** Type of Vault */
        environment: String
    ) {
        this.context = context
        tracker = CollectActionTracker(
            id,
            environment,
            UUID.randomUUID().toString()
        )

        baseURL = id.setupURL(environment)
        initializeCollect()
    }

    private fun initializeCollect() {
        client = ApiClient.newHttpClient()
        storage = InternalStorage(context, storageErrorListener)
    }


    init {
        externalDependencyDispatcher = DependencyReceiver()
        addOnResponseListeners(analyticListener)
    }

    /**
     * Adds a listener to the list of those whose methods are called whenever the VGSCollect receive response from Server.
     *
     * @param onResponseListener Interface definition for a receiving callback.
     */
    fun addOnResponseListeners(onResponseListener: VgsCollectResponseListener?) {
        onResponseListener?.let {
            if (!responseListeners.contains(it)) responseListeners.add(it)
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
    fun removeOnResponseListener(onResponseListener: VgsCollectResponseListener) {
        if (responseListeners.contains(onResponseListener)) responseListeners.remove(
            onResponseListener
        )
    }

    /**
     * Allows VGS secure fields to interact with [VGSCollect] and collect data from this source.
     *
     * @param view base class for VGS secure fields.
     */
    fun bindView(view: InputFieldView?) {
        view?.statePreparer?.let {
            externalDependencyDispatcher.addDependencyListener(
                view.getFieldName(),
                it.getDependencyListener()
            )

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
    fun addOnFieldStateChangeListener(fieldStateListener: OnFieldStateChangeListener?) {
        storage.attachStateChangeListener(fieldStateListener)
    }

    /**
     * Clear all information collected before by VGSCollect.
     * Preferably call it inside onDestroy system's callback.
     */
    fun onDestroy() {
        client.cancelAll()
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
    fun submit(path: String, method: HTTPMethod = HTTPMethod.POST): VGSResponse {
        val request = VGSRequest.VGSRequestBuilder()
            .setPath(path)
            .setMethod(method)
            .build()

        return submit(request)
    }

    /**
     * This method executes and send data on VGS Server. It could be useful if you want to handle
     * multithreading by yourself.
     * Do not use this method on the UI thread as this may crash.
     *
     * @param request data class with attributes for submit.
     */
    fun submit(request: VGSRequest): VGSResponse {
        var response: VGSResponse = VGSResponse.ErrorResponse()

        collectUserData(request) {
            response = client.execute(
                request.toNetworkRequest(baseURL)
            ).toVGSResponse(context)
        }

        return response
    }

    /**
     * This method executes and send data on VGS Server.
     *
     * @param path path for a request
     * @param method HTTP method
     */
    fun asyncSubmit(
        path: String, method: HTTPMethod
    ) {
        val request = VGSRequest.VGSRequestBuilder()
            .setPath(path)
            .setMethod(method)
            .build()

        asyncSubmit(request)
    }

    /**
     * This method executes and send data on VGS Server.
     *
     * @param request data class with attributes for submit
     */
    fun asyncSubmit(request: VGSRequest) {
        collectUserData(request) {
            client.enqueue(request.toNetworkRequest(baseURL)) { r ->
                mainHandler.post { notifyAllListeners(r.toVGSResponse()) }
            }
        }
    }

    private fun collectUserData(request: VGSRequest, submitRequest: () -> Unit) {
        when {
            !request.fieldsIgnore && !validateFields() -> return
            !request.fileIgnore && !validateFiles() -> return
            !baseURL.isURLValid() -> notifyAllListeners(VGSError.URL_NOT_VALID.toVGSResponse(context))
            !context.hasInternetPermission() ->
                notifyAllListeners(VGSError.NO_INTERNET_PERMISSIONS.toVGSResponse(context))
            !context.hasAccessNetworkStatePermission() ->
                notifyAllListeners(VGSError.NO_NETWORK_CONNECTIONS.toVGSResponse(context))
            !context.isConnectionAvailable() ->
                notifyAllListeners(VGSError.NO_NETWORK_CONNECTIONS.toVGSResponse(context))
            else -> {
                mergeData(request.customData, request.fieldsIgnore, request.fileIgnore)
                submitEvent(
                    true,
                    !request.fileIgnore,
                    !request.fieldsIgnore,
                    request.customHeader.isNotEmpty(),
                    request.customData.isNotEmpty(),
                    hasCustomHostname
                )
                submitRequest()
            }
        }
    }

    private fun notifyAllListeners(r: VGSResponse) {
        responseListeners.forEach { it.onResponse(r) }
    }

    private fun validateFiles(): Boolean {
        var isValid = true

        storage.getAttachedFiles().forEach {
            if (it.size > storage.getFileSizeLimit()) {
                notifyAllListeners(VGSError.FILE_SIZE_OVER_LIMIT.toVGSResponse(context, it.name))

                isValid = false
                return@forEach
            }
        }

        return isValid
    }

    private fun validateFields(): Boolean {
        var isValid = true

        storage.getFieldsStorage().getItems().forEach {
            if (it.isValid.not()) {
                VGSError.INPUT_DATA_NOT_VALID.toVGSResponse(context, it.fieldName).also { r ->
                    notifyAllListeners(r)
                    Logger.e(VGSCollect::class.java, r.localizeMessage)
                    submitEvent(false, code = r.errorCode)
                }

                isValid = false
                return isValid
            }
        }
        return isValid
    }

    private fun mergeData(
        customData: HashMap<String, Any> = HashMap(),
        fieldsIgnore: Boolean = false,
        fileIgnore: Boolean = false
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
            ?.run { customData.deepMerge(this) }
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

        if (resultCode == Activity.RESULT_OK) {
            val map: VGSHashMapWrapper<String, Any?>? = data?.extras?.getParcelable(
                BaseTransmitActivity.RESULT_DATA
            )

            if (requestCode == TemporaryFileStorage.REQUEST_CODE) {
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
            ) ?: VGSHashMapWrapper()

            when (map.get(BaseTransmitActivity.RESULT_TYPE)) {
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
        } ?: mutableMapOf()

        tracker.logEvent(
            InitAction(m)
        )
    }

    private fun scanEvent(status: String, type: String, id: String?) {
        val m = with(mutableMapOf<String, String>()) {
            put("status", status)
            put("scannerType", type)
            if (!id.isNullOrEmpty()) put("scanId", id.toString())

            this
        }
        tracker.logEvent(
            ScanAction(m)
        )
    }

    private fun submitEvent(
        isSuccess: Boolean,
        hasFiles: Boolean = false,
        hasFields: Boolean = false,
        hasCustomHeader: Boolean = false,
        hasCustomData: Boolean = false,
        hasCustomHostname: Boolean = false,
        code: Int = 200
    ) {
        if (code.isHttpStatusCode()) {
            val m = with(mutableMapOf<String, Any>()) {
                put("status", isSuccess.toAnalyticStatus())

                put("statusCode", code)

                val arr = with(mutableListOf<String>()) {
                    if (hasCustomHostname) add("customHostName")
                    if (hasFiles) add("file")
                    if (hasFields) add("fields")
                    if (hasCustomHeader ||
                        client.getTemporaryStorage().getCustomHeaders().isNotEmpty()
                    ) add("customHeaders")
                    if (hasCustomData ||
                        client.getTemporaryStorage().getCustomData().isNotEmpty()
                    ) add("customData")
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

    private fun responseEvent(code: Int, message: String? = null) {
        if (code.isHttpStatusCode()) {
            val m = with(mutableMapOf<String, Any>()) {
                put("statusCode", code)
                put("status", BaseTransmitActivity.Status.SUCCESS.raw)
                if (!message.isNullOrEmpty()) put("error", message)

                this
            }
            tracker.logEvent(
                ResponseAction(m)
            )
        }
    }

    private fun attachFileEvent(status: String) {//MIME, success
        val m = with(mutableMapOf<String, Any>()) {
            put("status", status)

            this
        }
        tracker.logEvent(
            AttachFileAction(m)
        )
    }

    private var hasCustomHostname = false

    private fun configureHostname(host: String?, tnt: String) {
        if (!host.isNullOrBlank()) {
            val r = VGSRequest.VGSRequestBuilder()
                .setMethod(HTTPMethod.GET)
                .setFormat(VGSHttpBodyFormat.PLAIN_TEXT)
                .build()
                .toNetworkRequest(
                    host.toHostnameValidationUrl(tnt)
                )

            client.enqueue(r) {
                hasCustomHostname = it.isSuccessful && host equalsUrl it.body
                if (hasCustomHostname) {
                    client.setHost(it.body)
                } else {
                    Logger.e(context, VGSCollect::class.java, R.string.error_custom_host_wrong)
                }

                hostnameValidationEvent(hasCustomHostname, host)
            }
        }
    }

    private fun hostnameValidationEvent(
        isSuccess: Boolean,
        hostname: String = ""
    ) {
        val m = with(mutableMapOf<String, Any>()) {
            put("status", isSuccess.toAnalyticStatus())
            put("hostname", hostname)

            this
        }

        tracker.logEvent(
            HostNameValidationAction(m)
        )
    }

    class Builder(

        /** Activity context */
        private val context: Context,

        /** Specific Vault ID  */
        private val id: String
    ) {

        private var environment: String = Environment.SANDBOX.rawValue

        private var host: String? = null

        /** Specify Environment for the VGSCollect instance. */
        fun setEnvironment(env: Environment, region: String = ""): Builder = this.apply {
            environment = env.rawValue concatWithDash region
        }

        /** Specify Environment for the VGSCollect instance. */
        fun setEnvironment(env: Environment): Builder = this.apply {
            environment = env.rawValue
        }

        /**
         * Specify Environment for the VGSCollect instance.
         * Also, Environment could be used with region prefix ( sandbox-eu-0 ).
         */
        fun setEnvironment(env: String): Builder = this.apply { environment = env }

        /** Sets the VGSCollect instance to use the custom hostname. */
        fun setHostname(cname: String): Builder {
            if (cname.isURLValid()) {
                host = cname.toHost()
            }

            if (host != cname) Logger.w(
                VGSCollect::class.java,
                "Hostname will be normalized to the $host"
            )

            return this
        }

        /**
         * Creates an VGSCollect with the arguments supplied to this
         * builder.
         */
        fun create(): VGSCollect {
            return VGSCollect(context, id, environment).apply {
                configureHostname(host, id)
            }
        }
    }
}
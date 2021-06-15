package com.verygoodsecurity.vgscollect.core

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.annotation.IntRange
import androidx.annotation.VisibleForTesting
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.VGSCollectLogger
import com.verygoodsecurity.vgscollect.app.BaseTransmitActivity
import com.verygoodsecurity.vgscollect.core.api.*
import com.verygoodsecurity.vgscollect.core.api.analityc.AnalyticTracker
import com.verygoodsecurity.vgscollect.core.api.analityc.CollectActionTracker
import com.verygoodsecurity.vgscollect.core.api.analityc.action.*
import com.verygoodsecurity.vgscollect.core.api.analityc.utils.toAnalyticStatus
import com.verygoodsecurity.vgscollect.core.api.client.ApiClient
import com.verygoodsecurity.vgscollect.core.api.client.ApiClient.Companion.generateAgentHeader
import com.verygoodsecurity.vgscollect.core.api.client.extension.isHttpStatusCode
import com.verygoodsecurity.vgscollect.core.model.VGSCollectFieldNameMappingPolicy
import com.verygoodsecurity.vgscollect.core.model.VGSCollectFieldNameMappingPolicy.*
import com.verygoodsecurity.vgscollect.core.model.VGSHashMapWrapper
import com.verygoodsecurity.vgscollect.core.model.network.*
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.model.state.mapToFieldState
import com.verygoodsecurity.vgscollect.core.storage.*
import com.verygoodsecurity.vgscollect.core.storage.content.file.StorageErrorListener
import com.verygoodsecurity.vgscollect.core.storage.content.file.TemporaryFileStorage
import com.verygoodsecurity.vgscollect.core.storage.content.file.VGSFileProvider
import com.verygoodsecurity.vgscollect.core.storage.external.DependencyReceiver
import com.verygoodsecurity.vgscollect.core.storage.external.ExternalDependencyDispatcher
import com.verygoodsecurity.vgscollect.util.*
import com.verygoodsecurity.vgscollect.util.extension.*
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.card.getAnalyticName
import java.util.*

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

    private var client: ApiClient
    private val mainHandler: Handler = Handler(Looper.getMainLooper())

    private var storage: InternalStorage
    private val storageErrorListener: StorageErrorListener = object : StorageErrorListener {
        override fun onStorageError(error: VGSError) {
            error.toVGSResponse(context).also { r ->
                notifyAllListeners(r)
                VGSCollectLogger.warn(InputFieldView.TAG, r.localizeMessage)
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

    private val baseURL: String
    private val context: Context

    private var cname: String? = null
    private var isSatelliteMode: Boolean = false

    private constructor(
        context: Context,
        id: String,
        environment: String,
        url: String?,
        port: Int?
    ) {
        this.context = context
        this.storage = InternalStorage(context, storageErrorListener)
        this.externalDependencyDispatcher = DependencyReceiver()
        this.client = ApiClient.newHttpClient()
        this.baseURL = generateBaseUrl(id, environment, url, port)
        this.tracker =
            CollectActionTracker(id, environment, UUID.randomUUID().toString(), isSatelliteMode)
        cname?.let { configureHostname(it, id) }
        updateAgentHeader()
        addOnResponseListeners(analyticListener)
    }

    constructor(
        /** Activity context */
        context: Context,

        /** Unique Vault id */
        id: String,

        /** Type of Vault */
        environment: String
    ) : this(context, id, environment, null, null)

    constructor(
        /** Activity context */
        context: Context,

        /** Unique Vault id */
        id: String,

        /** Type of Vault */
        environment: Environment = Environment.SANDBOX
    ) : this(context, id, environment.rawValue, null, null)

    constructor(
        /** Activity context */
        context: Context,

        /** Unique Vault id */
        id: String,

        /** Type of Environment */
        environmentType: String,

        /** Region identifier */
        suffix: String
    ) : this(context, id, environmentType concatWithDash suffix, null, null)

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
     * Allows VGS secure fields to interact with [VGSCollect] and collect data from this source.
     *
     * @param views VGS secure views.
     */
    fun bindView(vararg views: InputFieldView?) {
        views.forEach {
            bindView(it)
        }
    }

    /**
     * Allows to unsubscribe from a View updates.
     *
     * @param view base class for VGS secure fields.
     */
    fun unbindView(view: InputFieldView?) {
        view?.let {
            storage.unsubscribe(view)
        }
    }

    /**
     * Allows to unsubscribe from a View updates.
     *
     * @param views VGS secure views.
     */
    fun unbindView(vararg views: InputFieldView?) {
        views.forEach {
            unbindView(it)
        }
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
                request.toNetworkRequest(baseURL, it)
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
            client.enqueue(request.toNetworkRequest(baseURL, it)) { r ->
                mainHandler.post { notifyAllListeners(r.toVGSResponse()) }
            }
        }
    }

    private fun collectUserData(request: VGSRequest, submitRequest: (Map<String, Any>) -> Unit) {
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
                val data = mergeData(request)
                submitEvent(
                    true,
                    !request.fileIgnore && storage.getFileStorage().getItems().isNotEmpty(),
                    !request.fieldsIgnore && storage.getFieldsStorage().getItems().isNotEmpty(),
                    request.customHeader.isNotEmpty(),
                    data.isNotEmpty(),
                    hasCustomHostname,
                    request.fieldNameMappingPolicy
                )
                submitRequest(data)
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
                    VGSCollectLogger.warn(InputFieldView.TAG, r.localizeMessage)
                    submitEvent(false, code = r.errorCode)
                }

                isValid = false
                return isValid
            }
        }
        return isValid
    }

    private fun mergeData(request: VGSRequest): Map<String, Any> {
        val (allowArrays, mergeArraysPolicy) = when (request.fieldNameMappingPolicy) {
            FLAT_JSON -> null to ArrayMergePolicy.OVERWRITE
            NESTED_JSON -> false to ArrayMergePolicy.OVERWRITE
            NESTED_JSON_WITH_ARRAYS_MERGE -> true to ArrayMergePolicy.MERGE
            NESTED_JSON_WITH_ARRAYS_OVERWRITE -> true to ArrayMergePolicy.OVERWRITE
        }

        return with(client.getTemporaryStorage().getCustomData()) { // Static additional data
            // Merge dynamic additional data
            deepMerge(request.customData, mergeArraysPolicy)

            val fieldsData = allowArrays?.let {
                storage.getAssociatedList(request.fieldsIgnore, request.fileIgnore)
                    .toFlatMap(it).structuredData
            } ?: storage.getAssociatedList(request.fieldsIgnore, request.fileIgnore).toMap()

            deepMerge(fieldsData, mergeArraysPolicy)
        }
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

    /**
     * If you want to disable collecting analytics from VGS Collect SDK, you can set the value to false.
     * This helps us to understand which areas require improvements.
     * No personal information is tracked.
     *
     * Warning: if this option is set to false, it will increase resolving time for possible incidents.
     */
    fun setAnalyticsEnabled(isEnabled: Boolean) {
        tracker.isEnabled = isEnabled
        updateAgentHeader()
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
        mappingPolicy: VGSCollectFieldNameMappingPolicy = NESTED_JSON,
        code: Int = 200
    ) {
        if (code.isHttpStatusCode()) {
            val m = with(mutableMapOf<String, Any>()) {
                put("status", isSuccess.toAnalyticStatus())

                put("statusCode", code)

                val arr = with(mutableListOf<String>()) {
                    if (hasCustomHostname) add("custom_hostname")
                    if (hasFiles) add("file")
                    if (hasFields) add("textField")
                    if (hasCustomHeader ||
                        client.getTemporaryStorage().getCustomHeaders().isNotEmpty()
                    ) add("custom_header")
                    if (hasCustomData ||
                        client.getTemporaryStorage().getCustomData().isNotEmpty()
                    ) add("custom_data")
                    add(mappingPolicy.analyticsName)
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

    private fun generateBaseUrl(id: String, environment: String, url: String?, port: Int?): String {

        fun printPortDenied() {
            if (port.isValidPort()) {
                VGSCollectLogger.warn(message = context.getString(R.string.error_custom_port_is_not_allowed))
            }
        }

        if (!url.isNullOrBlank() && url.isURLValid()) {
            val host = getHost(url)
            if (host.isValidIp()) {
                if (!host.isIpAllowed()) {
                    VGSCollectLogger.warn(message = context.getString(R.string.error_custom_ip_is_not_allowed))
                    return id.setupURL(environment)
                }
                if (!environment.isSandbox()) {
                    VGSCollectLogger.warn(message = context.getString(R.string.error_env_incorrect))
                    return id.setupURL(environment)
                }
                isSatelliteMode = true
                return host.setupLocalhostURL(port)
            } else {
                printPortDenied()
                cname = host
                return id.setupURL(environment)
            }
        } else {
            printPortDenied()
            return id.setupURL(environment)
        }
    }

    private fun getHost(url: String) = url.toHost().also {
        if (it != url) {
            VGSCollectLogger.debug(message = "Hostname will be normalized to the $it")
        }
    }

    private fun configureHostname(host: String, tnt: String) {
        if (host.isNotBlank() && baseURL.isNotEmpty()) {
            val r = VGSRequest.VGSRequestBuilder()
                .setMethod(HTTPMethod.GET)
                .setFormat(VGSHttpBodyFormat.PLAIN_TEXT)
                .build()
                .toNetworkRequest(host.toHostnameValidationUrl(tnt))

            client.enqueue(r) {
                hasCustomHostname = it.isSuccessful && host equalsUrl it.body
                if (hasCustomHostname) {
                    client.setHost(it.body)
                } else {
                    context.run {
                        VGSCollectLogger.warn(
                            message = String.format(
                                getString(R.string.error_custom_host_wrong),
                                host
                            )
                        )
                    }
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

    private fun updateAgentHeader() {
        client.getTemporaryStorage().setCustomHeaders(mapOf(generateAgentHeader(tracker.isEnabled)))
    }

    /**
     * Used to create VGSCollect instances with default and overridden settings.
     *
     * @constructor Main constrictor for creating VGSCollect instance builder.
     * @param context Activity context.
     * @param id Specific Vault ID.
     */
    class Builder(private val context: Context, private val id: String) {

        private var environment: String = Environment.SANDBOX.rawValue
        private var host: String? = null
        private var port: Int? = null

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

        /**
         * Sets the VGSCollect instance to use the custom hostname.
         * Also, the localhost IP can be used for VGS-Satellite for local testing.
         *
         * @param cname where VGSCollect will send requests.
         */
        fun setHostname(cname: String): Builder = this.apply {
            if (!cname.isURLValid()) {
                VGSCollectLogger.warn(message = context.getString(R.string.error_custom_host_wrong_short))
                return@apply
            }
            this.host = cname
        }

        /**
         * Sets the VGSCollect instance to use the custom hostname port.
         * Port can be used only with localhost with VGS-Satellite, otherwise, it will be ignored.
         *
         * @param port Integer value from 1 to 65353.
         */
        fun setPort(
            @IntRange(from = PORT_MIN_VALUE, to = PORT_MAX_VALUE) port: Int
        ) = this.apply { this.port = port }

        /**
         * Creates an VGSCollect with the arguments supplied to this
         * builder.
         */
        fun create() = VGSCollect(context, id, environment, host, port)
    }
}
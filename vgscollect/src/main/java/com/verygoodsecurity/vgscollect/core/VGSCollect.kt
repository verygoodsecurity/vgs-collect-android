package com.verygoodsecurity.vgscollect.core

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.annotation.IntRange
import androidx.annotation.VisibleForTesting
import com.verygoodsecurity.sdk.analytics.AnalyticsManager
import com.verygoodsecurity.sdk.analytics.model.Event
import com.verygoodsecurity.sdk.analytics.model.Status
import com.verygoodsecurity.sdk.analytics.model.Upstream
import com.verygoodsecurity.vgscollect.BuildConfig
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.VGSCollectLogger
import com.verygoodsecurity.vgscollect.app.BaseTransmitActivity
import com.verygoodsecurity.vgscollect.core.api.PORT_MAX_VALUE
import com.verygoodsecurity.vgscollect.core.api.PORT_MIN_VALUE
import com.verygoodsecurity.vgscollect.core.api.VGSHttpBodyFormat
import com.verygoodsecurity.vgscollect.core.api.client.ApiClient
import com.verygoodsecurity.vgscollect.core.api.client.ApiClient.Companion.generateAgentHeader
import com.verygoodsecurity.vgscollect.core.api.client.extension.isCodeSuccessful
import com.verygoodsecurity.vgscollect.core.api.client.extension.isHttpStatusCode
import com.verygoodsecurity.vgscollect.core.api.equalsUrl
import com.verygoodsecurity.vgscollect.core.api.isIpAllowed
import com.verygoodsecurity.vgscollect.core.api.isURLValid
import com.verygoodsecurity.vgscollect.core.api.isValidIp
import com.verygoodsecurity.vgscollect.core.api.isValidPort
import com.verygoodsecurity.vgscollect.core.api.setupLocalhostURL
import com.verygoodsecurity.vgscollect.core.api.setupURL
import com.verygoodsecurity.vgscollect.core.api.toHost
import com.verygoodsecurity.vgscollect.core.api.toHostnameValidationUrl
import com.verygoodsecurity.vgscollect.core.model.VGSCollectFieldNameMappingPolicy
import com.verygoodsecurity.vgscollect.core.model.VGSCollectFieldNameMappingPolicy.NESTED_JSON
import com.verygoodsecurity.vgscollect.core.model.VGSHashMapWrapper
import com.verygoodsecurity.vgscollect.core.model.network.VGSBaseRequest
import com.verygoodsecurity.vgscollect.core.model.network.VGSError
import com.verygoodsecurity.vgscollect.core.model.network.VGSRequest
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.network.toVGSResponse
import com.verygoodsecurity.vgscollect.core.model.network.tokenization.VGSTokenizationRequest
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.model.state.mapToFieldState
import com.verygoodsecurity.vgscollect.core.storage.InternalStorage
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import com.verygoodsecurity.vgscollect.core.storage.content.file.StorageErrorListener
import com.verygoodsecurity.vgscollect.core.storage.content.file.TemporaryFileStorage
import com.verygoodsecurity.vgscollect.core.storage.content.file.VGSFileProvider
import com.verygoodsecurity.vgscollect.core.storage.external.DependencyReceiver
import com.verygoodsecurity.vgscollect.core.storage.external.ExternalDependencyDispatcher
import com.verygoodsecurity.vgscollect.util.extension.DATA_KEY
import com.verygoodsecurity.vgscollect.util.extension.concatWithDash
import com.verygoodsecurity.vgscollect.util.extension.hasAccessNetworkStatePermission
import com.verygoodsecurity.vgscollect.util.extension.hasInternetPermission
import com.verygoodsecurity.vgscollect.util.extension.isConnectionAvailable
import com.verygoodsecurity.vgscollect.util.extension.prepareUserDataForCollecting
import com.verygoodsecurity.vgscollect.util.extension.toAnalyticsMappingPolicy
import com.verygoodsecurity.vgscollect.util.extension.toAnalyticsStatus
import com.verygoodsecurity.vgscollect.util.extension.toNetworkRequest
import com.verygoodsecurity.vgscollect.util.extension.toTokenizationData
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.card.getAnalyticName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.CopyOnWriteArrayList

private const val SOURCE_TAG = "androidSDK"

/**
 * VGS Collect allows you to securely collect data and files from your users without having
 * to have them pass through your systems.
 * Entry-point to the Collect SDK.
 *
 * @since 1.0.0
 */
class VGSCollect {

    private val externalDependencyDispatcher: ExternalDependencyDispatcher

    private val analyticsManager: AnalyticsManager
    private var isAnalyticsEnabled: Boolean = true
    private var client: ApiClient
    private val mainHandler: Handler = Handler(Looper.getMainLooper())

    private var storage: InternalStorage
    private val storageErrorListener: StorageErrorListener = object : StorageErrorListener {
        override fun onStorageError(error: VGSError) {
            error.toVGSResponse().also { r ->
                notifyAllListeners(r, false)
                VGSCollectLogger.warn(InputFieldView.TAG, r.localizeMessage)
                submitEvent(isSuccess = false, requiresTokenization = false, code = r.errorCode)
            }
        }
    }

    private val responseListeners = CopyOnWriteArrayList<VgsCollectResponseListener>()

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
        this.analyticsManager =
            AnalyticsManager(id, environment, SOURCE_TAG, BuildConfig.VERSION_NAME)
        this.storage = InternalStorage(context, storageErrorListener)
        this.externalDependencyDispatcher = DependencyReceiver()
        this.client = ApiClient.newHttpClient()
        this.baseURL = generateBaseUrl(id, environment, url, port)
        cname?.let { configureHostname(it, id) }
        updateAgentHeader()
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
     * @param views VGS secure views.
     */
    fun bindView(vararg views: InputFieldView?) {
        views.forEach {
            bindView(it)
        }
    }

    /**
     * Allows VGS secure fields to interact with [VGSCollect] and collect data from this source.
     *
     * @param view base class for VGS secure fields.
     */
    fun bindView(view: InputFieldView?) {
        bindView(view, isCompose = false)
    }

    /**
     * Allows to unsubscribe from a View updates.
     *
     * @param view base class for VGS secure fields.
     */
    fun unbindView(view: InputFieldView?) {
        view?.let { storage.unsubscribe(view) }
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
        analyticsManager.cancelAll()
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
     * The method sends data on VGS Server for tokenization.
     */
    fun tokenize() {
        with(VGSTokenizationRequest.VGSRequestBuilder().build()) {
            submitAsyncRequest(this)
        }
    }

    /**
     * The method sends data on VGS Server for tokenization.
     *
     * @param request A tokenization request data.
     */
    fun tokenize(request: VGSTokenizationRequest) {
        submitAsyncRequest(request)
    }

    /**
     * This suspend method executes and send data on VGS Server on IO dispatcher.
     *
     * @param path path for a request
     * @param method HTTP method
     */
    suspend fun submitAsync(
        path: String,
        method: HTTPMethod = HTTPMethod.POST
    ): VGSResponse = submitAsync(
        VGSRequest.VGSRequestBuilder()
            .setPath(path)
            .setMethod(method)
            .build()
    )

    /**
     * This suspend method executes and send data on VGS Server on IO dispatcher.
     *
     * @param request data class with attributes for submit
     */
    suspend fun submitAsync(
        request: VGSRequest
    ): VGSResponse = withContext(Dispatchers.IO) {
        submit(request)
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
        submitAsyncRequest(request)
    }

    private fun submitAsyncRequest(request: VGSBaseRequest) {
        collectUserData(request) {
            client.enqueue(request.toNetworkRequest(baseURL, it)) { r ->
                mainHandler.post {
                    notifyAllListeners(
                        r.toVGSResponse(),
                        request.requiresTokenization
                    )
                }
            }
        }
    }

    private fun collectUserData(
        request: VGSBaseRequest,
        submitRequest: (Map<String, Any>) -> Unit
    ) {
        when {
            !request.fieldsIgnore && !validateFields(request.requiresTokenization) -> return
            !request.fileIgnore && !validateFiles(request.requiresTokenization) -> return
            !baseURL.isURLValid() -> notifyAllListeners(
                VGSError.URL_NOT_VALID.toVGSResponse(),
                request.requiresTokenization
            )

            !context.hasInternetPermission() ->
                notifyAllListeners(
                    VGSError.NO_INTERNET_PERMISSIONS.toVGSResponse(),
                    request.requiresTokenization
                )

            !context.hasAccessNetworkStatePermission() ->
                notifyAllListeners(
                    VGSError.NO_NETWORK_CONNECTIONS.toVGSResponse(),
                    request.requiresTokenization
                )

            !context.isConnectionAvailable() ->
                notifyAllListeners(
                    VGSError.NO_NETWORK_CONNECTIONS.toVGSResponse(),
                    request.requiresTokenization
                )

            else -> {
                val data = prepareDataToSubmit(request)

                submitEvent(
                    true,
                    request.requiresTokenization,
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

    private fun prepareDataToSubmit(request: VGSBaseRequest): Map<String, Any> {
        return request.takeIf { it.requiresTokenization }
            ?.run { prepareDataForTokenization() }
            ?: prepareDataForCollecting(request as VGSRequest)
    }

    private fun notifyAllListeners(r: VGSResponse, requiresTokenization: Boolean) {
        responseEvent(
            r.code,
            requiresTokenization,
            (r as? VGSResponse.ErrorResponse)?.localizeMessage
        )
        responseListeners.forEach { it.onResponse(r) }
    }

    private fun validateFiles(requiresTokenization: Boolean): Boolean {
        var isValid = true

        storage.getAttachedFiles().forEach {
            if (it.size > storage.getFileSizeLimit()) {
                notifyAllListeners(
                    VGSError.FILE_SIZE_OVER_LIMIT.toVGSResponse(it.name),
                    requiresTokenization
                )

                isValid = false
                return@forEach
            }
        }

        return isValid
    }

    private fun validateFields(requiresTokenization: Boolean): Boolean {
        var isValid = true

        storage.getFieldsStorage().getItems().forEach {
            if (it.isValid.not()) {
                VGSError.INPUT_DATA_NOT_VALID.toVGSResponse(it.fieldName).also { r ->
                    notifyAllListeners(r, requiresTokenization)
                    VGSCollectLogger.warn(InputFieldView.TAG, r.localizeMessage)
                    submitEvent(false, requiresTokenization, code = r.errorCode)
                }

                isValid = false
                return isValid
            }
        }
        return isValid
    }

    private fun prepareDataForCollecting(request: VGSRequest) =
        request.prepareUserDataForCollecting(
            client.getTemporaryStorage().getCustomData(),
            storage.getData(
                request.fieldNameMappingPolicy,
                request.fieldsIgnore,
                request.fileIgnore
            )
        )

    private fun prepareDataForTokenization(): MutableMap<String, Any> {
        val tokenizationItems = mutableListOf<Map<String, Any>>()
        storage.getFieldsStorage().getItems().forEach {
            tokenizationItems.addAll(it.toTokenizationData())
        }
        return mutableMapOf(DATA_KEY to tokenizationItems)
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
                    map.get(BaseTransmitActivity.RESULT_STATUS) as BaseTransmitActivity.Status,
                    map.get(BaseTransmitActivity.RESULT_NAME).toString(),
                    map.get(BaseTransmitActivity.RESULT_ID) as? String,
                    map.get(BaseTransmitActivity.RESULT_DETAILS).toString()
                )

                BaseTransmitActivity.ATTACH -> attachFileEvent(
                    map.get(BaseTransmitActivity.RESULT_STATUS) as BaseTransmitActivity.Status
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
        isAnalyticsEnabled = isEnabled
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

    internal fun bindComposeView(view: InputFieldView?) {
        bindView(view, isCompose = true)
    }

    private fun bindView(view: InputFieldView?, isCompose: Boolean) {
        view?.let {
            externalDependencyDispatcher.addDependencyListener(
                view.getFieldName(),
                it.statePreparer.getDependencyListener()
            )

            it.statePreparer.setAnalyticManager(analyticsManager)
            storage.performSubscription(view)
            fieldInitEvent(it, isCompose)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun fieldInitEvent(view: InputFieldView, isCompose: Boolean) {
        capture(
            Event.FieldInit(
                fieldType = view.getFieldType().getAnalyticName(),
                contentPath = view.getFieldName() ?: "",
            )
        ) // TODO: Add UI type
    }

    private fun scanEvent(
        status: BaseTransmitActivity.Status,
        type: String,
        id: String?,
        details: String? = null
    ) {
        capture(
            Event.Scan(
                status = status.toAnalyticsStatus(),
                scanId = id.toString(),
                scanDetails = details.toString(),
                scannerType = type
            )
        )
    }

    private fun submitEvent(
        isSuccess: Boolean,
        requiresTokenization: Boolean,
        hasFiles: Boolean = false,
        hasFields: Boolean = false,
        hasCustomHeader: Boolean = false,
        hasCustomData: Boolean = false,
        hasCustomHostname: Boolean = false,
        mappingPolicy: VGSCollectFieldNameMappingPolicy = NESTED_JSON,
        code: Int = 200
    ) {
        if (code.isHttpStatusCode()) {
            val event = Event.Request.Builder(
                status = if (isSuccess) Status.OK else Status.FAILED,
                code = code,
                upstream = if (requiresTokenization) Upstream.TOKENIZATION else Upstream.CUSTOM
            )

            if (hasCustomHostname) event.customHostname()
            if (hasFiles) event.files()
            if (hasFields) event.fields()
            if (hasCustomHeader || client.getTemporaryStorage().getCustomHeaders().isNotEmpty()) {
                event.customHeader()
            }
            if (hasCustomData || client.getTemporaryStorage().getCustomData().isNotEmpty()) {
                event.customData()
            }

            event.mappingPolicy(mappingPolicy.toAnalyticsMappingPolicy())

            capture(event.build())
        }
    }

    private fun responseEvent(code: Int, requiresTokenization: Boolean, message: String? = null) {
        if (code.isHttpStatusCode()) {
            capture(
                Event.Response(
                    status = code.isCodeSuccessful().toAnalyticsStatus(),
                    code = code,
                    upstream = if (requiresTokenization) Upstream.TOKENIZATION else Upstream.CUSTOM
                )
            )
        }
    }

    private fun attachFileEvent(status: BaseTransmitActivity.Status) {
        capture(Event.AttachFile(status.toAnalyticsStatus()))
    }

    private fun hostnameValidationEvent(
        isSuccess: Boolean,
        hostname: String = ""
    ) {
        analyticsManager.capture(
            Event.Cname(
                status = isSuccess.toAnalyticsStatus(),
                hostname = hostname
            )
        )
    }

    private fun updateAgentHeader() {
        client.getTemporaryStorage()
            .setCustomHeaders(mapOf(generateAgentHeader(isAnalyticsEnabled)))
    }

    private fun capture(event: Event) {
        if (isAnalyticsEnabled) {
            analyticsManager.capture(event)
        }
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
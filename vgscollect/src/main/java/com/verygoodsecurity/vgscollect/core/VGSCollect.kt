package com.verygoodsecurity.vgscollect.core

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.annotation.VisibleForTesting
import androidx.core.os.BundleCompat
import com.verygoodsecurity.sdk.analytics.VGSSharedAnalyticsManager
import com.verygoodsecurity.sdk.analytics.model.VGSAnalyticsEvent
import com.verygoodsecurity.sdk.analytics.model.VGSAnalyticsScannerType
import com.verygoodsecurity.sdk.analytics.model.VGSAnalyticsStatus
import com.verygoodsecurity.sdk.analytics.model.VGSAnalyticsUpstream
import com.verygoodsecurity.vgscollect.BuildConfig
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.VGSCollectLogger
import com.verygoodsecurity.vgscollect.app.BaseTransmitActivity
import com.verygoodsecurity.vgscollect.core.api.VGSHttpBodyFormat
import com.verygoodsecurity.vgscollect.core.api.client.ApiClient
import com.verygoodsecurity.vgscollect.core.api.client.ApiClient.Companion.generateAgentHeader
import com.verygoodsecurity.vgscollect.core.api.client.extension.isCodeSuccessful
import com.verygoodsecurity.vgscollect.core.api.equalsUrl
import com.verygoodsecurity.vgscollect.core.api.isURLValid
import com.verygoodsecurity.vgscollect.core.api.setupCardManagerURL
import com.verygoodsecurity.vgscollect.core.api.setupURL
import com.verygoodsecurity.vgscollect.core.api.toHost
import com.verygoodsecurity.vgscollect.core.api.toHostnameValidationUrl
import com.verygoodsecurity.vgscollect.core.model.CardAttributesConfig
import com.verygoodsecurity.vgscollect.core.model.VGSCollectFieldNameMappingPolicy
import com.verygoodsecurity.vgscollect.core.model.VGSCollectFieldNameMappingPolicy.NESTED_JSON
import com.verygoodsecurity.vgscollect.core.model.VGSHashMapWrapper
import com.verygoodsecurity.vgscollect.core.model.network.NetworkRequest
import com.verygoodsecurity.vgscollect.core.model.network.NetworkResponse
import com.verygoodsecurity.vgscollect.core.model.network.VGSBaseRequest
import com.verygoodsecurity.vgscollect.core.model.network.VGSError
import com.verygoodsecurity.vgscollect.core.model.network.VGSRequest
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.network.cmp.VGSCardManagementPlatformRequest
import com.verygoodsecurity.vgscollect.core.model.network.cmp.VGSCreateCardRequest
import com.verygoodsecurity.vgscollect.core.model.network.cmp.VGSUpdateCardRequest
import com.verygoodsecurity.vgscollect.core.model.network.toVGSResponse
import com.verygoodsecurity.vgscollect.core.model.network.tokenization.VGSCreateAliasesRequest
import com.verygoodsecurity.vgscollect.core.model.network.tokenization.VGSTokenizationRequest
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.model.state.mapToFieldState
import com.verygoodsecurity.vgscollect.core.storage.InternalStorage
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import com.verygoodsecurity.vgscollect.core.storage.content.file.StorageListener
import com.verygoodsecurity.vgscollect.core.storage.content.file.TemporaryFileStorage
import com.verygoodsecurity.vgscollect.core.storage.content.file.VGSFileProvider
import com.verygoodsecurity.vgscollect.core.storage.external.DependencyReceiver
import com.verygoodsecurity.vgscollect.core.storage.external.ExternalDependencyDispatcher
import com.verygoodsecurity.vgscollect.util.NetworkInspector
import com.verygoodsecurity.vgscollect.util.extension.DEFAULT_CONNECTION_TIME_OUT
import com.verygoodsecurity.vgscollect.util.extension.concatWithDash
import com.verygoodsecurity.vgscollect.util.extension.toAnalyticsMappingPolicy
import com.verygoodsecurity.vgscollect.util.extension.toAnalyticsStatus
import com.verygoodsecurity.vgscollect.util.extension.toHex
import com.verygoodsecurity.vgscollect.util.extension.toNetworkRequest
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.card.getAnalyticName
import com.verygoodsecurity.vgscollect.widget.compose.state.core.BaseFieldState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList

private const val SOURCE_TAG = "androidSDK"
private const val DEPENDENCY_MANAGER = "maven"

/**
 * VGS Collect allows you to securely collect data and files from your users without having
 * to have them pass through your systems.
 * Entry-point to the Collect SDK.
 *
 * @since 1.0.0
 */
class VGSCollect {

    private val context: Context
    internal val tenantId: String
    internal val environment: String
    internal val collectURL: String
    internal val cardManagementURL: String
    private var cardAttributesManager: CardAttributesManager? = null
    private val authHandler: VgsAuthHandler?
    private val formId: String = UUID.randomUUID().toString()
    private var sessionFormId: String? = null
    private val externalDependencyDispatcher: ExternalDependencyDispatcher

    private val analyticsManager: VGSSharedAnalyticsManager
    internal val analyticsHandler: AnalyticsHandler

    private var client: ApiClient
    private var mainHandler: Handler = Handler(Looper.getMainLooper())

    private var storage: InternalStorage
    private val storageErrorListener: StorageListener = object : StorageListener {

        override fun onStorageError(
            error: VGSError,
            upstream: VGSAnalyticsUpstream,
            vararg params: String?
        ) {
            error.toVGSResponse(*params).also { response ->
                VGSCollectLogger.warn(InputFieldView.TAG, response.localizeMessage)
                requestEvent(isSuccess = false, upstream = upstream, code = response.errorCode)
                notifyAllListeners(response, upstream)
            }
        }
    }

    private val responseListeners = CopyOnWriteArrayList<VgsCollectResponseListener>()

    private var hasCustomHostname = false

    private constructor(
        context: Context,
        id: String,
        env: String,
        suffix: String?,
        cname: String?,
        collectInitAnalytics: Boolean = true,
        authHandler: VgsAuthHandler? = null
    ) {
        this.context = context
        this.tenantId = id
        this.environment = suffix?.let { env concatWithDash it } ?: env
        this.collectURL = tenantId.setupURL(environment)
        this.cardManagementURL = setupCardManagerURL(environment)
        this.analyticsManager =
            VGSSharedAnalyticsManager(SOURCE_TAG, BuildConfig.VERSION_NAME, DEPENDENCY_MANAGER)
        this.analyticsHandler = object : AnalyticsHandler {

            override fun capture(event: VGSAnalyticsEvent) {
                analyticsManager.capture(
                    vault = tenantId,
                    environment = environment,
                    formId = formId,
                    event = event
                )
            }
        }
        this.storage = InternalStorage(this.context, storageErrorListener)
        this.externalDependencyDispatcher = DependencyReceiver()
        this.client = ApiClient.build(NetworkInspector(this.context))
        this.authHandler = authHandler
        configureHostname(getHost(cname), tenantId)
        updateAgentHeader()
        if (collectInitAnalytics) {
            analyticsHandler.capture(VGSAnalyticsEvent.Init.create())
        }
    }

    constructor(
        /** Activity context */
        context: Context,

        /** Unique Tenant id */
        id: String,

        /** Type of Vault */
        environment: String
    ) : this(context, id, environment, null, null)

    constructor(
        /** Activity context */
        context: Context,

        /** Unique Tenant id */
        id: String,

        /** Type of Vault */
        environment: Environment = Environment.SANDBOX
    ) : this(context, id, environment.rawValue, null, null)

    constructor(
        /** Activity context */
        context: Context,

        /** Unique Tenant id */
        id: String,

        /** Type of Environment */
        environmentType: String,

        /** Region identifier */
        suffix: String
    ) : this(context, id, environmentType, suffix, null)

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
     * This method removes a listener whose methods are called whenever VGS secure fields state changes.
     *
     * @param fieldStateListener listener which will be removed.
     */
    fun removeOnFieldStateChangeListener(fieldStateListener: OnFieldStateChangeListener?) {
        storage.detachStateChangeListener(fieldStateListener)
    }

    /**
     * Sets a listener to receive card attributes lookup events.
     *
     * @param listener the listener to be notified of lookup start, success,
     * and failure events, or `null` to clear the current listener.
     */
    fun setCardAttributesLookupListener(listener: VgsCardAttributesLookupListener?) {
        cardAttributesManager?.setCardAttributesLookupListener(listener)
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
        cardAttributesManager?.dispose()
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
     * This function executes and send data on VGS Server. It could be useful if you want to handle
     * multithreading by yourself.
     * Do not use this method on the UI thread as this may crash.
     *
     * @param path path for a request
     * @param method HTTP method
     * @param fieldsStates Optional list of Compose field states to tokenize.
     */
    fun submit(
        path: String,
        method: HTTPMethod = HTTPMethod.POST,
        fieldsStates: List<BaseFieldState>? = null
    ): VGSResponse = submit(
        request = VGSRequest.VGSRequestBuilder().setPath(path).setMethod(method).build(),
        fieldsStates = fieldsStates
    )

    /**
     * This function executes and send data on VGS Server. It could be useful if you want to handle
     * multithreading by yourself.
     * Do not use this method on the UI thread as this may crash.
     *
     * @param request data class with attributes for submit.
     * @param fieldsStates Optional list of Compose field states to tokenize.
     */
    fun submit(
        request: VGSRequest,
        fieldsStates: List<BaseFieldState>? = null
    ): VGSResponse {
        val data = storage.getDataForCollecting(
            request = request,
            staticData = client.getTemporaryStorage().getCustomData(),
            fieldsStates = fieldsStates
        )
        return request(request, data)
    }

    /**
     * This suspends function executes and send data on VGS Server on IO dispatcher.
     *
     * @param path path for a request
     * @param method HTTP method
     * @param fieldsStates Optional list of Compose field states to tokenize.
     */
    suspend fun submitAsync(
        path: String,
        method: HTTPMethod = HTTPMethod.POST,
        fieldsStates: List<BaseFieldState>? = null
    ): VGSResponse = submitAsync(
        request = VGSRequest.VGSRequestBuilder().setPath(path).setMethod(method).build(),
        fieldsStates = fieldsStates
    )

    /**
     * This suspends function executes and send data on VGS Server on IO dispatcher.
     *
     * @param request data class with attributes for submit
     * @param fieldsStates Optional list of Compose field states to tokenize.
     */
    suspend fun submitAsync(
        request: VGSRequest,
        fieldsStates: List<BaseFieldState>? = null
    ): VGSResponse = withContext(Dispatchers.IO) {
        submit(request, fieldsStates)
    }

    /**
     * This method executes and send data on VGS Server.
     *
     * @param path path for a request`
     * @param method HTTP method
     * @param fieldsStates Optional list of Compose field states to tokenize.
     */
    fun asyncSubmit(
        path: String,
        method: HTTPMethod,
        fieldsStates: List<BaseFieldState>? = null
    ) {
        asyncSubmit(
            request = VGSRequest.VGSRequestBuilder().setPath(path).setMethod(method).build(),
            fieldsStates = fieldsStates
        )
    }

    /**
     * This method executes and send data on VGS Server.
     *
     * @param request data class with attributes for submit
     * @param fieldsStates Optional list of Compose field states to tokenize.
     */
    fun asyncSubmit(request: VGSRequest, fieldsStates: List<BaseFieldState>? = null) {
        val data = storage.getDataForCollecting(
            request = request,
            staticData = client.getTemporaryStorage().getCustomData(),
            fieldsStates = fieldsStates
        )
        requestAsync(request, data)
    }

    /**
     * The method sends data on VGS Server for tokenization. It is an asynchronous method.
     * @param fieldsStates Optional list of Compose field states to tokenize.
     */
    fun tokenize(fieldsStates: List<BaseFieldState>? = null) {
        tokenize(
            request = VGSTokenizationRequest.VGSRequestBuilder().build(),
            fieldsStates = fieldsStates
        )
    }

    /**
     * The method sends data on VGS Server for tokenization. It is an asynchronous method.
     *
     * @param request A tokenization request data.
     * @param fieldsStates Optional list of Compose field states to tokenize.
     */
    fun tokenize(request: VGSTokenizationRequest, fieldsStates: List<BaseFieldState>? = null) {
        requestAsync(
            request = request,
            data = storage.getDataForTokenization(
                request.fieldsIgnore,
                fieldsStates,
            )
        )
    }

    /**
     * The method sends data on VGS Server for create aliases. It is an asynchronous method.
     *
     * @param fieldsStates Optional list of Compose field states to tokenize.
     */
    fun createAliases(fieldsStates: List<BaseFieldState>? = null) {
        createAliases(
            request = VGSCreateAliasesRequest.VGSRequestBuilder().build(),
            fieldsStates = fieldsStates
        )
    }

    /**
     * The method sends data on VGS Server for create aliases. It is an asynchronous method.
     *
     * @param request A create aliases request data.
     * @param fieldsStates List of Compose field states to tokenize.
     */
    fun createAliases(
        request: VGSCreateAliasesRequest,
        fieldsStates: List<BaseFieldState>? = null
    ) {
        requestAsync(
            request = request,
            data = storage.getDataForTokenization(
                request.fieldsIgnore,
                fieldsStates,
            )
        )
    }

    /**
     * Creates a new card using the [Card Management API](https://www.verygoodsecurity.com/docs/api/card-management#tag/card-management/POST/cards).
     *
     * @param auth Optional authentication token used for the request. AuthHandler used otherwise.
     * @param fieldsStates List of Compose field states to tokenize.
     */
    fun createCard(auth: String? = null, fieldsStates: List<BaseFieldState>? = null) {
        getAccessToken(auth) { token ->
            val request = VGSCreateCardRequest.VGSRequestBuilder()
                .setAuthToken(token)
                .build()
            cmpRequest(request, fieldsStates)
        }
    }

    /**
     * Creates a new card using the [Card Management API](https://www.verygoodsecurity.com/docs/api/card-management#tag/card-management/POST/cards).
     *
     * @param auth Optional authentication token used for the request. AuthHandler used otherwise.
     * @param fieldsStates List of Compose field states to tokenize.
     */
    fun updateCard(
        cardId: String,
        auth: String? = null,
        fieldsStates: List<BaseFieldState>? = null
    ) {
        getAccessToken(auth) { token ->
            val request = VGSUpdateCardRequest.VGSRequestBuilder(cardId)
                .setAuthToken(token)
                .build()
            cmpRequest(request, fieldsStates)
        }
    }

    private fun getAccessToken(providedToken: String?, onResult: (String) -> Unit) {
        providedToken?.let { onResult(it) }
            ?: authHandler?.requestToken { token -> onResult(token) }
    }

    private fun cmpRequest(
        request: VGSCardManagementPlatformRequest,
        fieldsStates: List<BaseFieldState>? = null
    ) {
        if (!cardManagementURL.isURLValid()) {
            notifyAllListeners(VGSError.URL_NOT_VALID.toVGSResponse(), request.upstream)
            return
        }
        storage.getDataForCmp(
            request = request,
            sessionFormId = sessionFormId,
            staticData = client.getTemporaryStorage().getCustomData(),
            fieldsStates = fieldsStates
        )?.let { data ->
            client.enqueue(request.toNetworkRequest(cardManagementURL, data)) { response ->
                mainHandler.post {
                    notifyAllListeners(response.toVGSResponse(), request.upstream)
                }
            }
        }
    }

    private fun request(request: VGSBaseRequest, data: Map<String, Any>?): VGSResponse {
        return data?.let { payload ->
            requestEvent(
                true,
                request.upstream,
                !request.fileIgnore && storage.fileStorage.getItems().isNotEmpty(),
                !request.fieldsIgnore && storage.fieldsStorage.getItems().isNotEmpty(),
                request.customHeader.isNotEmpty(),
                payload.isNotEmpty(),
                hasCustomHostname,
                request.fieldNameMappingPolicy
            )
            client.execute(request.toNetworkRequest(collectURL, payload)).toVGSResponse()
        } ?: VGSResponse.ErrorResponse()
    }

    private fun requestAsync(request: VGSBaseRequest, data: Map<String, Any>?) {
        data?.let { payload ->
            requestEvent(
                true,
                request.upstream,
                !request.fileIgnore && storage.fileStorage.getItems().isNotEmpty(),
                !request.fieldsIgnore && storage.fieldsStorage.getItems().isNotEmpty(),
                request.customHeader.isNotEmpty(),
                payload.isNotEmpty(),
                hasCustomHostname,
                request.fieldNameMappingPolicy
            )
            client.enqueue(request.toNetworkRequest(collectURL, payload)) { response ->
                mainHandler.post {
                    notifyAllListeners(response.toVGSResponse(), request.upstream)
                }
            }
        }
    }

    private fun notifyAllListeners(response: VGSResponse, upstream: VGSAnalyticsUpstream) {
        responseEvent(
            response.code,
            upstream,
            (response as? VGSResponse.ErrorResponse)?.localizeMessage
        )
        responseListeners.forEach { it.onResponse(response) }
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
            val map = getResultDataMap(data)

            if (requestCode == TemporaryFileStorage.REQUEST_CODE) {
                map?.run {
                    storage.fileStorage.dispatch(mapOf())
                }
            } else {
                map?.run {
                    externalDependencyDispatcher.dispatch(mapOf())
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getResultDataMap(intent: Intent?): VGSHashMapWrapper<String, Any?>? {
        return BundleCompat.getParcelable(
            intent?.extras ?: return null,
            BaseTransmitActivity.RESULT_DATA,
            VGSHashMapWrapper::class.java
        ) as? VGSHashMapWrapper<String, Any?>
    }

    private fun mapAnalyticEvent(data: Intent?) {
        data?.let {
            val map = getResultDataMap(data) ?: VGSHashMapWrapper()

            when (map.get(BaseTransmitActivity.RESULT_TYPE)) {
                BaseTransmitActivity.SCAN -> scanEvent(
                    map.get(BaseTransmitActivity.RESULT_STATUS) as BaseTransmitActivity.Status,
                    map.get(BaseTransmitActivity.RESULT_NAME).toString(),
                    map.get(BaseTransmitActivity.RESULT_ID) as? String,
                    map.get(BaseTransmitActivity.RESULT_DETAILS) as? String
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
        return storage.fileProvider
    }

    /**
     * If you want to disable collecting analytics from VGS Collect SDK, you can set the value to false.
     * This helps us to understand which areas require improvements.
     * No personal information is tracked.
     *
     * Warning: if this option is set to false, it will increase resolving time for possible incidents.
     */
    fun setAnalyticsEnabled(isEnabled: Boolean) {
        analyticsManager.setIsEnabled(isEnabled)
        updateAgentHeader()
    }

    /**
     * Return true if analytics is enabled, false otherwise.
     */
    fun getIsAnalyticsEnabled() = analyticsManager.getIsEnabled()

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

    @VisibleForTesting
    internal fun setMainHandler(handler: Handler) {
        this.mainHandler = handler
    }

    internal fun bindComposeView(view: InputFieldView?) {
        bindView(view, isCompose = true)
    }

    private fun bindView(view: InputFieldView?, isCompose: Boolean) {
        view?.let {
            externalDependencyDispatcher.addDependencyListener(
                view.getFieldName(), it.statePreparer.getDependencyListener()
            )

            it.statePreparer.setAnalyticHandler(analyticsHandler)
            storage.performSubscription(view)
            fieldInitEvent(it, isCompose)
        }
    }

    private fun fieldInitEvent(view: InputFieldView, isCompose: Boolean) {
        analyticsHandler.capture(
            VGSAnalyticsEvent.FieldAttach(
                fieldType = view.getFieldType().getAnalyticName(),
                contentPath = null,
                ui = if (isCompose) "compose" else "xml",
            )
        )
    }

    private fun scanEvent(
        status: BaseTransmitActivity.Status,
        type: String,
        id: String?,
        details: String?
    ) {
        val scannerType = if (type == VGSAnalyticsScannerType.CARD_IO.analyticsValue) {
            VGSAnalyticsScannerType.CARD_IO
        } else {
            VGSAnalyticsScannerType.BLINK_CARD
        }

        analyticsHandler.capture(
            VGSAnalyticsEvent.Scan(
                status = status.toAnalyticsStatus(),
                scannerType = scannerType,
                scanId = id,
                scanDetails = details
            )
        )
    }

    private fun requestEvent(
        isSuccess: Boolean,
        upstream: VGSAnalyticsUpstream,
        hasFiles: Boolean = false,
        hasFields: Boolean = false,
        hasCustomHeader: Boolean = false,
        hasCustomData: Boolean = false,
        hasCustomHostname: Boolean = false,
        mappingPolicy: VGSCollectFieldNameMappingPolicy = NESTED_JSON,
        code: Int = 200
    ) {
        val event = VGSAnalyticsEvent.Request.Builder(
            status = if (isSuccess) VGSAnalyticsStatus.OK else VGSAnalyticsStatus.FAILED,
            code = code,
            upstream = upstream
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

        analyticsHandler.capture(event = event.build())
    }

    private fun responseEvent(code: Int, upstream: VGSAnalyticsUpstream, message: String? = null) {
        analyticsHandler.capture(
            VGSAnalyticsEvent.Response(
                status = code.isCodeSuccessful().toAnalyticsStatus(),
                code = code,
                upstream = upstream,
                errorMessage = message
            )
        )
    }

    private fun attachFileEvent(status: BaseTransmitActivity.Status) {
        analyticsHandler.capture(
            VGSAnalyticsEvent.AttachFile(status.toAnalyticsStatus())
        )
    }

    private fun hostnameValidationEvent(isSuccess: Boolean, hostname: String = "") {
        analyticsHandler.capture(
            VGSAnalyticsEvent.Cname(
                status = isSuccess.toAnalyticsStatus(),
                hostname = hostname
            )
        )
    }

    private fun updateAgentHeader() {
        client.getTemporaryStorage()
            .setCustomHeaders(mapOf(generateAgentHeader(analyticsManager.getIsEnabled())))
    }

    private fun getHost(url: String?) = url?.toHost().also {
        if (it != url) {
            VGSCollectLogger.debug(message = "Hostname will be normalized to the $it")
        }
    }

    private fun configureHostname(host: String?, tenantId: String) {
        if (host.isNullOrBlank() || tenantId.isBlank() || collectURL.isEmpty()) {
            return
        }
        val r = VGSRequest.VGSRequestBuilder().setMethod(HTTPMethod.GET)
            .setFormat(VGSHttpBodyFormat.PLAIN_TEXT).build()
            .toNetworkRequest(host.toHostnameValidationUrl(tenantId))

        client.enqueue(r) {
            hasCustomHostname = it.isSuccessful && host equalsUrl it.body
            if (hasCustomHostname) {
                client.setHost(it.body)
            } else {
                context.run {
                    VGSCollectLogger.warn(
                        message = String.format(
                            getString(R.string.error_custom_host_wrong), host
                        )
                    )
                }
            }

            hostnameValidationEvent(hasCustomHostname, host)
        }
    }

    companion object {

        private const val SESSION_CONFIGS_BASE_URL = "https://js.verygoodvault.com"

        /**
         * Asynchronously creates and initializes a [VGSCollect] instance.
         *
         * @param context activity context.
         * @param tenantId unique vault identifier.
         * @param formId optional form identifier. If null or blank, [VGSCollect] is returned without session-based card attributes support.
         * @param environment target environment (e.g. sandbox, live).
         * @param authHandler provider responsible for supplying an auth token (required only when [formId] is provided).
         * @param onSuccess called with a fully initialized [VGSCollect] instance.
         * @param onError called if initialization fails.
         */
        fun session(
            context: Context,
            tenantId: String,
            formId: String? = null,
            environment: String,
            authHandler: VgsAuthHandler? = null,
            onSuccess: (instance: VGSCollect) -> Unit,
            onError: (code: Int, message: String?) -> Unit
        ) {
            val collect = VGSCollect(
                context = context,
                id = tenantId,
                env = environment,
                suffix = null,
                cname = null,
                collectInitAnalytics = false,
                authHandler = authHandler
            )
            if (formId.isNullOrBlank()) {
                sendAnalytics(
                    collect,
                    configFileName = null,
                    configFileStatusCode = null,
                    configFileLatency = null
                )
                onSuccess(collect)
                return
            }

            if (authHandler == null) {
                onError(
                    VGSError.AUTH_HANDLER_IS_REQUIRED.code,
                    VGSError.AUTH_HANDLER_IS_REQUIRED.message
                )
                return
            }
            val configFileName = "${formId.toHex()}.json"
            getConfig(collect, configFileName) { response ->
                sendAnalytics(
                    collect = collect,
                    configFileName = configFileName,
                    configFileStatusCode = response.code,
                    configFileLatency = response.latency
                )
                collect.mainHandler.post {
                    if (response.isSuccessful) {
                        CardAttributesConfig.parse(response.body)?.let { config ->
                            onSuccess(collect.apply {
                                this.cardAttributesManager = CardAttributesManager(
                                    context,
                                    environment,
                                    config,
                                    authHandler,
                                    storage,
                                    analyticsHandler
                                )
                                this.sessionFormId = formId
                            })
                        } ?: onError(
                            VGSError.CONFIGURATION_LOADING_FAILED.code,
                            VGSError.CONFIGURATION_LOADING_FAILED.message
                        )
                    } else {
                        onError(response.code, response.message ?: response.error?.message)
                    }
                }
            }
        }

        private fun sendAnalytics(
            collect: VGSCollect,
            configFileName: String?,
            configFileStatusCode: Int?,
            configFileLatency: Long?
        ) {
            collect.analyticsHandler.capture(
                VGSAnalyticsEvent.Init.session(
                    configFileName,
                    configFileStatusCode,
                    configFileLatency
                )
            )
        }

        private fun getConfig(
            collect: VGSCollect,
            configFileName: String,
            onResult: (NetworkResponse) -> Unit
        ) {
            collect.client.enqueue(
                request = NetworkRequest(
                    method = HTTPMethod.GET,
                    url = "$SESSION_CONFIGS_BASE_URL/session-configuration/${collect.tenantId}/$configFileName",
                    customHeader = emptyMap(),
                    customData = Unit,
                    format = VGSHttpBodyFormat.JSON,
                    requestTimeoutInterval = DEFAULT_CONNECTION_TIME_OUT,
                    requiresTokenization = false,
                ),
                callback = onResult
            )
        }
    }

    /**
     * Used to create VGSCollect instances with default and overridden settings.
     *
     * @constructor Main constrictor for creating VGSCollect instance builder.
     * @param context Activity context.
     * @param id Specific Tenant ID.
     */
    class Builder(private val context: Context, private val id: String) {

        private var environment: String = Environment.SANDBOX.rawValue
        private var cname: String? = null

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
         *
         * @param cname where VGSCollect will send requests.
         */
        fun setHostname(cname: String): Builder = this.apply {
            if (!cname.isURLValid()) {
                VGSCollectLogger.warn(message = context.getString(R.string.error_custom_host_wrong_short))
                return@apply
            }
            this.cname = cname
        }

        /**
         * Creates an VGSCollect with the arguments supplied to this
         * builder.
         */
        fun create() = VGSCollect(context, id, environment, null, cname)
    }
}
package com.verygoodsecurity.vgscollect.core

import android.content.Context
import com.verygoodsecurity.sdk.analytics.model.VGSAnalyticsEvent
import com.verygoodsecurity.vgscollect.VGSCollectLogger
import com.verygoodsecurity.vgscollect.core.api.VGSHttpBodyFormat
import com.verygoodsecurity.vgscollect.core.api.client.ApiClient
import com.verygoodsecurity.vgscollect.core.api.isEnvironmentValid
import com.verygoodsecurity.vgscollect.core.model.CardAttributesConfig
import com.verygoodsecurity.vgscollect.core.model.network.NetworkRequest
import com.verygoodsecurity.vgscollect.core.model.network.NetworkResponse
import com.verygoodsecurity.vgscollect.core.model.network.VGSError
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.storage.InternalStorage
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import com.verygoodsecurity.vgscollect.util.NetworkInspector
import com.verygoodsecurity.vgscollect.util.extension.DEFAULT_CONNECTION_TIME_OUT
import com.verygoodsecurity.vgscollect.util.extension.isAccessTokenFailureStatusCode
import com.verygoodsecurity.vgscollect.util.extension.toAnalyticsStatus
import com.verygoodsecurity.vgscollect.util.extension.toAuthHeader
import com.verygoodsecurity.vgscollect.util.extension.toJSON
import org.json.JSONObject

internal class CardAttributesManager(
    context: Context,
    environment: String,
    private val config: CardAttributesConfig,
    private val authHandler: VgsAuthHandler,
    private val storage: InternalStorage,
    private val analyticsHandler: AnalyticsHandler
) : OnFieldStateChangeListener {

    companion object {

        private const val TAG = "CardAttributesManager"

        private const val SANDBOX_URL = "https://card-enrichment-api.sandbox.verygoodvault.com/cardattributes/enriched"
        private const val LIVE_URL = "https://card-enrichment-api.verygoodvault.com/cardattributes/enriched"

        private const val CARD_ATTRIBUTES_BIN_LENGTH = 11

        private const val BIN_BODY_PATH = "number"
        private const val FILTERS_BODY_PATH = "filter"

        private const val REQUEST_TAG = "card-attributes-lookup-request"
    }

    private val url: String? = buildUrl(environment)

    private val client = ApiClient.build(NetworkInspector(context))

    private var listener: VgsCardAttributesLookupListener? = null
    private var cachedBin: String? = null
    private var cachedToken: String? = null

    init {
        storage.attachStateChangeListener(this)
    }

    override fun onStateChange(state: FieldState) {
        if (state !is FieldState.CardNumberState) return
        val cardState = storage.getFieldsStates().find { it.fieldName == state.fieldName } ?: return
        val bin = getCardBinForAttributesLookup(cardState.content?.rawData)
        val fieldName = cardState.fieldName ?: return
        if (bin.isNullOrBlank()) {
            cachedBin = bin
            return
        }
        if (shouldLookup(bin)) {
            cachedBin = bin
            startCardAttributesLookup(fieldName, bin, config.filters)
        }
    }

    fun setCardAttributesLookupListener(listener: VgsCardAttributesLookupListener?) {
        this.listener = listener
    }

    fun dispose() {
        storage.detachStateChangeListener(this)
        client.cancelByTag(REQUEST_TAG)
    }

    private fun shouldLookup(bin: String): Boolean {
        return config.isEnabled && config.filters.isNotEmpty() && bin != cachedBin
    }

    private fun getCardBinForAttributesLookup(raw: String?): String? {
        val digits = (raw ?: "").replace(Regex("\\D"), "")
        return if (digits.length < CARD_ATTRIBUTES_BIN_LENGTH) {
            null
        } else {
            digits.substring(0, CARD_ATTRIBUTES_BIN_LENGTH)
        }
    }

    private fun startCardAttributesLookup(
        fieldName: String,
        bin: String,
        filters: List<String>
    ) {
        client.cancelByTag(REQUEST_TAG)
        listener?.onStart()
        requestCardAttributes(CardAttributesRequestParams(url, fieldName, bin, filters))
    }

    private fun requestCardAttributes(parameters: CardAttributesRequestParams) {
        if (parameters.url.isNullOrBlank()) {
            listener?.onFailure(
                VGSError.URL_NOT_VALID.code,
                null,
                VGSError.URL_NOT_VALID.message
            )
            return
        }
        getAccessToken { token ->
            client.enqueue(
                request = NetworkRequest(
                    method = HTTPMethod.POST,
                    url = parameters.url,
                    customHeader = mapOf(token.toAuthHeader()),
                    customData = mapOf(
                        BIN_BODY_PATH to parameters.bin,
                        FILTERS_BODY_PATH to parameters.filters
                    ).toJSON().toString(),
                    format = VGSHttpBodyFormat.JSON,
                    requestTimeoutInterval = DEFAULT_CONNECTION_TIME_OUT,
                    requiresTokenization = false,
                    tag = REQUEST_TAG
                )
            ) { handleCardAttributesResult(parameters, it) }
        }
    }

    private fun getAccessToken(onResult: (String) -> Unit) {
        cachedToken?.let { onResult(it) } ?: authHandler.requestToken { token ->
            if (token.isBlank()) {
                listener?.onFailure(
                    VGSError.AUTH_TOKEN_IS_BLANK.code,
                    null,
                    VGSError.AUTH_TOKEN_IS_BLANK.message
                )
                return@requestToken
            }
            cachedToken = token
            onResult(token)
        }
    }

    private fun handleCardAttributesResult(
        parameters: CardAttributesRequestParams,
        response: NetworkResponse
    ) {
        sendAnalytics(response)
        if (response.isSuccessful) {
            handleCardAttributesRequestSucceed(response.code, response.body ?: "")
        } else {
            handleCardAttributesRequestFailed(
                parameters,
                response.code,
                response.body,
                response.message ?: response.error?.message
            )
        }
    }

    private fun sendAnalytics(response: NetworkResponse) {
        val error = try {
            JSONObject(response.body ?: "").getString("detail")
        } catch (_: Exception) {
            null
        }
        analyticsHandler.capture(
            VGSAnalyticsEvent.CardLookup(
                response.isSuccessful.toAnalyticsStatus(),
                response.code,
                response.latency,
                error
            )
        )
    }

    private fun handleCardAttributesRequestSucceed(code: Int, body: String) {
        listener?.onSuccess(code, body)
    }

    private fun handleCardAttributesRequestFailed(
        parameters: CardAttributesRequestParams,
        code: Int,
        body: String?,
        message: String?
    ) {
        if (code.isAccessTokenFailureStatusCode() && parameters.retryOnAuthFailure) {
            cachedToken = null
            requestCardAttributes(parameters.copy(retryOnAuthFailure = false))
        } else {
            listener?.onFailure(code, body, message)
        }
    }

    private fun buildUrl(environment: String): String? {
        if (environment.isBlank() || !environment.isEnvironmentValid()) {
            VGSCollectLogger.warn(
                tag = TAG,
                message = "Environment is blank or invalid. Card attributes URL cannot be built."
            )
            return null
        }
        return if (environment.contains(Environment.SANDBOX.rawValue, true)) {
            SANDBOX_URL
        } else {
            LIVE_URL
        }
    }

    private data class CardAttributesRequestParams(
        val url: String?,
        val fieldName: String,
        val bin: String,
        val filters: List<String>,
        val retryOnAuthFailure: Boolean = true
    )
}
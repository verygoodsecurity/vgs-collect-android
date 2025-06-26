package com.verygoodsecurity.vgscollect.core.api.client

import com.verygoodsecurity.sdk.analytics.utils.VGSAnalyticsSession
import com.verygoodsecurity.vgscollect.BuildConfig
import com.verygoodsecurity.vgscollect.core.api.VgsApiTemporaryStorage
import com.verygoodsecurity.vgscollect.core.api.VgsApiTemporaryStorageImpl
import com.verygoodsecurity.vgscollect.core.model.network.NetworkRequest
import com.verygoodsecurity.vgscollect.core.model.network.NetworkResponse

internal interface ApiClient {

    fun setHost(url: String?)

    fun enqueue(request: NetworkRequest, callback: ((NetworkResponse) -> Unit)? = null)

    fun execute(request: NetworkRequest): NetworkResponse

    fun cancelAll()

    fun getTemporaryStorage(): VgsApiTemporaryStorage

    companion object {

        private const val AGENT = "vgs-client"
        private const val TEMPORARY_AGENT_TEMPLATE =
            "source=androidSDK&medium=vgs-collect&content=%s&vgsCollectSessionId=%s&tr=%s"

        fun newHttpClient(
            isLogsVisible: Boolean = true,
            storage: VgsApiTemporaryStorage? = null
        ): ApiClient {
            return OkHttpClient(isLogsVisible, storage ?: VgsApiTemporaryStorageImpl())
        }

        fun generateAgentHeader(isAnalyticsEnabled: Boolean): Pair<String, String> =
            AGENT to String.format(
                TEMPORARY_AGENT_TEMPLATE,
                BuildConfig.VERSION_NAME,
                VGSAnalyticsSession.id,
                if (isAnalyticsEnabled) "default" else "none"
            )
    }
}
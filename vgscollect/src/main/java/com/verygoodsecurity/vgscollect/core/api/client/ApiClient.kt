package com.verygoodsecurity.vgscollect.core.api.client

import com.verygoodsecurity.sdk.analytics.utils.VGSAnalyticsSession
import com.verygoodsecurity.vgscollect.BuildConfig
import com.verygoodsecurity.vgscollect.core.api.VgsApiTemporaryStorage
import com.verygoodsecurity.vgscollect.core.model.network.NetworkRequest
import com.verygoodsecurity.vgscollect.core.model.network.NetworkResponse
import com.verygoodsecurity.vgscollect.util.NetworkInspector

internal interface ApiClient {

    fun setHost(url: String?)

    fun enqueue(request: NetworkRequest, callback: ((NetworkResponse) -> Unit)? = null)

    fun execute(request: NetworkRequest): NetworkResponse

    fun cancelAll()

    fun getTemporaryStorage(): VgsApiTemporaryStorage

    companion object {

        private const val AGENT = "vgs-client"
        private const val AGENT_TEMPLATE =
            "source=androidSDK&medium=vgs-collect&content=%s&vgsCollectSessionId=%s&tr=%s"

        fun build(inspector: NetworkInspector): ApiClient = OkHttpClient(inspector)

        fun generateAgentHeader(isAnalyticsEnabled: Boolean): Pair<String, String> {
            return AGENT to String.format(
                AGENT_TEMPLATE,
                BuildConfig.VERSION_NAME,
                VGSAnalyticsSession.id,
                if (isAnalyticsEnabled) "default" else "none"
            )
        }
    }
}
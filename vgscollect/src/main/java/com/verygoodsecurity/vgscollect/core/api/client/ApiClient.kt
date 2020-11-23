package com.verygoodsecurity.vgscollect.core.api.client

import android.os.Build
import com.verygoodsecurity.vgscollect.core.api.VgsApiTemporaryStorage
import com.verygoodsecurity.vgscollect.core.model.network.NetworkResponse
import com.verygoodsecurity.vgscollect.core.model.network.VGSRequest
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse

internal interface ApiClient {

    fun setURL(url: String)

    fun enqueue(request: VGSRequest, callback: ((NetworkResponse) -> Unit)? = null)
    fun execute(request: VGSRequest): NetworkResponse
    fun cancelAll()

    fun getTemporaryStorage(): VgsApiTemporaryStorage

    companion object {
        internal const val CONNECTION_TIME_OUT = 60000L
        internal const val AGENT = "vgs-client"
        internal const val CONTENT_TYPE = "Content-type"
        internal const val TEMPORARY_AGENT_TEMPLATE = "source=androidSDK&medium=vgs-collect&content=%s&vgsCollectSessionId=%s"

        fun newHttpClient(url: String): ApiClient {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                OkHttpClient().apply { setURL(url) }
            } else {
                URLConnectionClient.newInstance().apply { setURL(url) }
            }
        }

        fun newHttpClient(): ApiClient {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                OkHttpClient()
            } else {
                URLConnectionClient.newInstance()
            }
        }
    }
}
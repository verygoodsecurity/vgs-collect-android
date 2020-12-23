package com.verygoodsecurity.vgscollect.core.api.client

import android.os.Build
import com.verygoodsecurity.vgscollect.BuildConfig
import com.verygoodsecurity.vgscollect.core.api.VgsApiTemporaryStorage
import com.verygoodsecurity.vgscollect.core.api.VgsApiTemporaryStorageImpl
import com.verygoodsecurity.vgscollect.core.api.analityc.CollectActionTracker
import com.verygoodsecurity.vgscollect.core.model.network.NetworkRequest
import com.verygoodsecurity.vgscollect.core.model.network.NetworkResponse

internal interface ApiClient {

    fun setHost(url: String?)

    fun enqueue(request: NetworkRequest, callback: ((NetworkResponse) -> Unit)? = null)
    fun execute(request: NetworkRequest): NetworkResponse
    fun cancelAll()

    fun getTemporaryStorage(): VgsApiTemporaryStorage

    companion object {
        internal const val CONNECTION_TIME_OUT = 60000L
        internal const val AGENT = "vgs-client"
        internal const val CONTENT_TYPE = "Content-type"
        internal const val TEMPORARY_AGENT_TEMPLATE =
            "source=androidSDK&medium=vgs-collect&content=%s&vgsCollectSessionId=%s"

        fun newHttpClient(
            url: String,
            isLogsVisible: Boolean = true,
            storage: VgsApiTemporaryStorage? = null
        ): ApiClient {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                OkHttpClient(
                    isLogsVisible,
                    storage?:getInternalStorage()
                ).apply { setHost(url) }
            } else {
                URLConnectionClient.newInstance(
                    isLogsVisible,
                    storage?:getInternalStorage()
                ).apply { setHost(url) }
            }
        }

        fun newHttpClient(
            isLogsVisible: Boolean = true,
            storage: VgsApiTemporaryStorage? = null
        ): ApiClient {

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                OkHttpClient(
                    isLogsVisible,
                    storage?:getInternalStorage()
                )
            } else {
                URLConnectionClient.newInstance(
                    isLogsVisible,
                    storage?:getInternalStorage())
            }
        }

        private fun getInternalStorage(): VgsApiTemporaryStorage {
            return VgsApiTemporaryStorageImpl().apply {
                setCustomHeaders(
                    mapOf(
                        AGENT to String.format(
                            TEMPORARY_AGENT_TEMPLATE,
                            BuildConfig.VERSION_NAME,
                            CollectActionTracker.Sid.id
                        )
                    )
                )
            }
        }
    }
}
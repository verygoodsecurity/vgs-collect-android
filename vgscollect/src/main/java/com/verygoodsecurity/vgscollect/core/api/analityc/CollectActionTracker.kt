package com.verygoodsecurity.vgscollect.core.api.analityc

import android.os.Build
import com.verygoodsecurity.vgscollect.BuildConfig
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.api.VGSHttpBodyFormat
import com.verygoodsecurity.vgscollect.core.api.VgsApiTemporaryStorageImpl
import com.verygoodsecurity.vgscollect.core.api.analityc.action.Action
import com.verygoodsecurity.vgscollect.core.api.client.ApiClient
import com.verygoodsecurity.vgscollect.core.model.network.VGSRequest
import com.verygoodsecurity.vgscollect.core.model.network.toAnalyticRequest
import java.util.*
import java.util.concurrent.Executors

internal class CollectActionTracker(
    val tnt: String,
    val environment: String,
    val formId: String,
    val isSatelliteMode: Boolean
) : AnalyticTracker {

    override var isEnabled: Boolean = true

    internal object Sid {
        val id = "${UUID.randomUUID()}"
    }

    private val client: ApiClient by lazy {
        return@lazy ApiClient.newHttpClient(false, VgsApiTemporaryStorageImpl())
    }

    override fun logEvent(action: Action) {
        if (isEnabled) {
            val event = action.run {
                val sender = Event(client, tnt, environment, formId, isSatelliteMode)
                sender.map = getAttributes()
                sender
            }

            Executors.newSingleThreadExecutor().submit(event)
        }
    }

    private class Event(
        private val client: ApiClient,
        private val tnt: String,
        private val environment: String,
        private val formId: String,
        private val isSatelliteMode: Boolean
    ) : Runnable {

        var map: MutableMap<String, Any> = mutableMapOf()
            set(value) {
                field = value
                field.putAll(attachDefaultInfo(value))
            }

        private fun attachDefaultInfo(map: MutableMap<String, Any>): Map<String, Any> {
            return with(map) {
                this[SATELLITE] = isSatelliteMode
                this[VG_SESSION_ID] = Sid.id
                this[FORM_ID] = formId
                this[SOURCE] = SOURCE_TAG
                this[TIMESTAMP] = System.currentTimeMillis()
                this[TNT] = tnt
                this[ENVIRONMENT] = environment
                this[VERSION] = BuildConfig.VERSION_NAME
                if (!this.containsKey(STATUS)) {
                    this[STATUS] = STATUS_OK
                }

                val deviceInfo = mutableMapOf<String, String>()
                deviceInfo[PLATFORM] = PLATFORM_TAG
                deviceInfo[DEVICE] = Build.BRAND
                deviceInfo[DEVICE_MODEL] = Build.MODEL
                deviceInfo[OS] = Build.VERSION.SDK_INT.toString()
                this[USER_AGENT] = deviceInfo

                this
            }
        }

        override fun run() {
            val r = VGSRequest.VGSRequestBuilder()
                .setPath(ENDPOINT)
                .setMethod(HTTPMethod.POST)
                .setCustomData(map)
                .setFormat(VGSHttpBodyFormat.X_WWW_FORM_URLENCODED)
                .build()

            client.enqueue(r.toAnalyticRequest(URL))
        }

        companion object {
            private const val SATELLITE = "vgsSatellite"
            private const val FORM_ID = "formId"
            private const val VG_SESSION_ID = "vgsCollectSessionId"
            private const val TIMESTAMP = "localTimestamp"
            private const val TNT = "tnt"
            private const val ENVIRONMENT = "env"
            private const val VERSION = "version"
            private const val PLATFORM = "platform"
            private const val SOURCE = "source"
            private const val PLATFORM_TAG = "android"
            private const val SOURCE_TAG = "androidSDK"
            private const val DEVICE = "device"
            private const val DEVICE_MODEL = "deviceModel"
            private const val OS = "osVersion"
            private const val STATUS = "status"
            private const val STATUS_OK = "Ok"
            private const val USER_AGENT = "ua"
        }
    }

    companion object {
        private const val ENDPOINT = "/vgs"
        private const val URL = "https://vgs-collect-keeper.apps.verygood.systems"
    }
}
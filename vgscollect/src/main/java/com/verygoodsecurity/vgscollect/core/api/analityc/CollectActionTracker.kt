package com.verygoodsecurity.vgscollect.core.api.analityc

import android.content.Context
import android.os.Build
import android.util.Log
import com.verygoodsecurity.vgscollect.BuildConfig
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.api.AnalyticClient
import com.verygoodsecurity.vgscollect.core.api.AnalyticURLConnectionClient
import com.verygoodsecurity.vgscollect.core.api.ApiClient
import com.verygoodsecurity.vgscollect.core.api.analityc.action.Action
import com.verygoodsecurity.vgscollect.core.isLive
import java.util.*
import java.util.concurrent.Executors

internal class CollectActionTracker(
    context: Context,
    val tnt: String,
    val environment: String,
    val formId: String
) : AnalyticTracker {

    internal object Sid {
        val id =  "${UUID.randomUUID()}"
    }

    private val client:ApiClient by lazy {
        val url = if(environment.isLive()) {
            "https://vgs-collect-keeper.apps.verygood.systems"
        } else {
            "https://vgs-collect-keeper.verygoodsecurity.io"
        }

        return@lazy if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AnalyticClient.newInstance(context, url)
        } else {
            AnalyticURLConnectionClient.newInstance(url)
        }
    }

    override fun logEvent(action: Action) {
        val event = action.run {
            val sender = Event(client, tnt, environment, formId)
            sender.map = getAttributes()
            sender
        }

        Executors.newSingleThreadExecutor().submit(event)
    }

    private class Event(
        private val client:ApiClient,
        private val tnt: String,
        private val environment: String,
        private val formId: String
    ):Runnable {

        var map:MutableMap<String, Any> = mutableMapOf()
            set(value) {
                field = value
                field.putAll(attachDefaultInfo(value))
            }

        private fun attachDefaultInfo(map: MutableMap<String, Any>):Map<String, Any> {
            return with(map) {
                this[VG_SESSION_ID] = Sid.id
                this[FORM_ID] = formId
                this[SOURCE] = SOURCE_TAG
                this[TIMESTAMP] = System.currentTimeMillis()
                this[TNT] = tnt
                this[ENVIRONMENT] = environment
                this[VERSION] = BuildConfig.VERSION_NAME
                if(!this.containsKey(STATUS)) {
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
            client.call(ENDPOINT, HTTPMethod.POST, null, map)
        }

        companion object {
            private const val FORM_ID = "formId"
            private const val SESSION_ID = "sessionId"
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
        private const val  ENDPOINT = "/vgs"
    }
}
package com.verygoodsecurity.vgscollect.core

import android.os.AsyncTask
import android.util.Log
import com.verygoodsecurity.vgscollect.core.api.ApiClient
import com.verygoodsecurity.vgscollect.core.model.Payload
import com.verygoodsecurity.vgscollect.core.model.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.mapUsefulPayloads
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.core.model.state.mapToFieldState
import com.verygoodsecurity.vgscollect.core.storage.VgsStore
import com.verygoodsecurity.vgscollect.util.Logger
import com.verygoodsecurity.vgscollect.widget.VGSEditText
import org.jetbrains.annotations.TestOnly
import java.lang.StringBuilder
import java.lang.ref.WeakReference

abstract class AbstractVgsCollect(
    id: String,
    environment: Environment
) {
    var onResponseListener:VgsCollectResponseListener? = null

    internal abstract var storage:VgsStore
    internal abstract var client: ApiClient

    internal val baseURL:String

    private val tasks = mutableListOf<AsyncTask<Payload, Void, VGSResponse>>()

    private companion object {
        private const val DOMEN = "verygoodproxy.com"
        private const val DIVIDER = "."
        private const val SCHEME  = "https://"
    }

    init {
        val builder = StringBuilder(SCHEME)
            .append(id).append(DIVIDER)
            .append(environment.rawValue).append(DIVIDER)
            .append(DOMEN)

        baseURL = builder.toString()
    }

    abstract fun bindView(view: VGSEditText?)

    fun onDestroy() {
        tasks.forEach {
            it.cancel(true)
        }
        tasks.clear()
        storage.clear()
    }

    fun getAllStates() = storage.getStates().map { it.mapToFieldState() }

    protected fun doRequest(path: String,
                  method: HTTPMethod,
                  headers: Map<String, String>?,
                  data: MutableCollection<VGSFieldState>
    ) {
        val r = client.call(path, method, headers, data.mapUsefulPayloads())
        onResponseListener?.onResponse(r)
    }

    protected fun doAsyncRequest(path: String,
                                method: HTTPMethod,
                                headers: Map<String, String>?,
                                data: MutableCollection<VGSFieldState>
    ) {
        val p = Payload(
            path,
            method,
            headers,
            data.mapUsefulPayloads()
        )

        val task = DoAsync(onResponseListener) {
            it?.run {
                client.call(this.path, this.method, this.headers, this.data)
            } ?: VGSResponse.ErrorResponse("error:")  //fixme
        }

        tasks.add(task)

        task.execute(p)
    }

    @TestOnly
    internal fun doMainThreadRequest(path: String,
                            method: HTTPMethod,
                            headers: Map<String, String>?,
                            data: MutableCollection<VGSFieldState>
    ) {
        doRequest(path, method, headers, data)
    }

    class DoAsync(listener: VgsCollectResponseListener?, val handler: (arg: Payload?) -> VGSResponse) : AsyncTask<Payload, Void, VGSResponse>() {
        var onResponseListener: WeakReference<VgsCollectResponseListener>? = WeakReference<VgsCollectResponseListener>(listener)
        override fun doInBackground(vararg arg: Payload?): VGSResponse? {
            val param = if(!arg.isNullOrEmpty()) {
                arg[0]
            } else {
                null
            }

            return handler(param)
        }

        override fun onPostExecute(result: VGSResponse?) {
            super.onPostExecute(result)
            if(onResponseListener == null) {
                Logger.i("VGSCollect", "VgsCollectResponseListener not set")
            } else {
                onResponseListener?.get()?.onResponse(result)
            }
        }
    }
}
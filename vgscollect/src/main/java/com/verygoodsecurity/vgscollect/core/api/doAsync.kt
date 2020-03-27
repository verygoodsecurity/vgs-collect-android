package com.verygoodsecurity.vgscollect.core.api

import android.os.AsyncTask
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.Payload
import com.verygoodsecurity.vgscollect.core.model.VGSResponse
import java.lang.ref.WeakReference

class doAsync(listeners: MutableList<VgsCollectResponseListener>, val handler: (arg: Payload?) -> VGSResponse) : AsyncTask<Payload, Void, VGSResponse>() {
    var onResponseListeners: WeakReference<MutableList<VgsCollectResponseListener>>? = WeakReference(listeners)
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
        onResponseListeners?.get()?.forEach {
            it.onResponse(result)
        }
    }

    override fun onCancelled(result: VGSResponse?) {
        onResponseListeners?.clear()
        super.onCancelled(result)
    }
}
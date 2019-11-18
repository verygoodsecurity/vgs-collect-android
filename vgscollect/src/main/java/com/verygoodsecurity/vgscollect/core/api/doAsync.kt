package com.verygoodsecurity.vgscollect.core.api

import android.os.AsyncTask
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.Payload
import com.verygoodsecurity.vgscollect.core.model.VGSResponse
import com.verygoodsecurity.vgscollect.util.Logger
import java.lang.ref.WeakReference

class doAsync(listener: VgsCollectResponseListener?, val handler: (arg: Payload?) -> VGSResponse) : AsyncTask<Payload, Void, VGSResponse>() {
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
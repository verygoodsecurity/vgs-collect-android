package com.verygoodsecurity.vgscollect.app

import android.os.Handler
import android.os.Looper
import androidx.annotation.AnyThread
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.model.VGSHashMapWrapper

abstract class VGSDataAdapter {

    private val mainHandler = Handler(Looper.getMainLooper())

    internal var collect: VGSCollect? = null

    @AnyThread
    protected fun setData(data: Map<String, Any?>) {
        mainHandler.post { collect?.dispatchData(VGSHashMapWrapper(HashMap(data))) }
    }
}
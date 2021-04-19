package com.verygoodsecurity.vgscollect.app

import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.model.VGSHashMapWrapper

abstract class VGSDataAdapter {

    internal var collect: VGSCollect? = null

    protected fun setData(data: Map<String, Any?>) {
        collect?.dispatchData(VGSHashMapWrapper(HashMap(data)))
    }
}
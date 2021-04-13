package com.verygoodsecurity.vgscollect.app

import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.model.VGSHashMapWrapper

private const val NFC_ADAPTER_CLASSNAME = "VGSNFCAdapter"

abstract class VGSDataAdapter {

    internal var collect: VGSCollect? = null

    protected fun setData(data: Map<String, Any?>) {
        checkIsValidInheritance {
            collect?.dispatchData(VGSHashMapWrapper(HashMap(data)))
        }
    }

    private fun checkIsValidInheritance(action: () -> Unit) {
        when (val name = this::class.java.simpleName) {
            NFC_ADAPTER_CLASSNAME -> action.invoke()
            else -> throw IllegalArgumentException("This class($name) should not inherit from VGSDataAdapter!")
        }
    }
}
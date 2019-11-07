package com.verygoodsecurity.vgscollect.core

import com.verygoodsecurity.vgscollect.core.model.VGSResponse

interface VgsCollectResponseListener {
    fun onResponse(response: VGSResponse?)
}
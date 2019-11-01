package com.verygoodsecurity.vgscollect.core

import com.verygoodsecurity.vgscollect.core.model.SimpleResponse

interface VgsCollectResponseListener {
    fun onResponse(response: SimpleResponse?)
}
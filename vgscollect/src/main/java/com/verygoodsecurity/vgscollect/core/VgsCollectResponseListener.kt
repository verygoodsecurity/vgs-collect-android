package com.verygoodsecurity.vgscollect.core

import com.verygoodsecurity.vgscollect.core.data.SimpleResponse

interface VgsCollectResponseListener {
    fun onResponse(response: SimpleResponse?)
}
package com.verygoodsecurity.vgscollect.core.api

import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.model.VGSResponse

interface ApiClient {
    fun call(
        path: String,
        method: HTTPMethod,
        headers: Map<String, String>?,
        data: Map<String, Any>?
):VGSResponse

    fun getTemporaryStorage():VgsApiTemporaryStorage
}
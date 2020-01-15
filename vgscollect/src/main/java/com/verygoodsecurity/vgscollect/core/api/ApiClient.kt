package com.verygoodsecurity.vgscollect.core.api

import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.model.VGSResponse

interface ApiClient {
    fun call(
        path: String,
        method: HTTPMethod,
        data: Map<String, String>?,
        headers: Map<String, String>?
    ):VGSResponse

    fun getTemporaryStorage():VgsApiTemporaryStorage
}
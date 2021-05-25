package com.verygoodsecurity.vgscollect.core.model.network

import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.api.VGSHttpBodyFormat

data class NetworkRequest(
    val method: HTTPMethod,
    var url: String,
    val customHeader: Map<String, String>,
    val customData: Any,
    val fieldsIgnore: Boolean,
    val fileIgnore: Boolean,
    val format: VGSHttpBodyFormat,
    val requestTimeoutInterval: Long
)
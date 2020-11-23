package com.verygoodsecurity.vgscollect.core.model.network

import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.api.VGSHttpBodyFormat

data class NetworkRequest(
    val method: HTTPMethod,
    var url: String,
    val customHeader: HashMap<String, String>,
    val customData: HashMap<String, Any>,
    val fieldsIgnore: Boolean = false,
    val fileIgnore: Boolean = false,
    val format: VGSHttpBodyFormat = VGSHttpBodyFormat.JSON,
)
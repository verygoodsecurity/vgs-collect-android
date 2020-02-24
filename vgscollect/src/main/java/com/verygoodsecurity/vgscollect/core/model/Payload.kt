package com.verygoodsecurity.vgscollect.core.model

import com.verygoodsecurity.vgscollect.core.HTTPMethod

data class Payload(val path: String,
                   val method: HTTPMethod,
                   val headers: Map<String, String>?,
                   val data: Map<String, Any>?
)
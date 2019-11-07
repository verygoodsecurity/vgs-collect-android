package com.verygoodsecurity.vgscollect.core.api

import com.verygoodsecurity.vgscollect.core.HTTPMethod

data class Payload(val path: String,
                   val method: HTTPMethod,
                   val data: Map<String, String>?,
                   val headers: Map<String, String>?
)
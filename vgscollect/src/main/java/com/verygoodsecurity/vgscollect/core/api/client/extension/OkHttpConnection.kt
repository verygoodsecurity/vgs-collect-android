package com.verygoodsecurity.vgscollect.core.api.client.extension

import com.verygoodsecurity.vgscollect.core.HTTPMethod
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

internal fun String?.toRequestBodyOrNull(mediaType: MediaType?, method: HTTPMethod) =
    when (method) {
        HTTPMethod.GET -> null
        else -> this?.toRequestBody(mediaType) ?: RequestBody.EMPTY
    }

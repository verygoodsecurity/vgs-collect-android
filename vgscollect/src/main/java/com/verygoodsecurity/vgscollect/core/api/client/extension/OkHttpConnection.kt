package com.verygoodsecurity.vgscollect.core.api.client.extension

import com.verygoodsecurity.vgscollect.core.HTTPMethod
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.EMPTY_REQUEST

fun Request.Builder.setMethod(
    method: HTTPMethod,
    data: String?,
    mediaType: MediaType?
): Request.Builder {
    return if (method == HTTPMethod.POST) {
        post(data?.toRequestBody(mediaType) ?: EMPTY_REQUEST)
    } else {
        return this
    }
}

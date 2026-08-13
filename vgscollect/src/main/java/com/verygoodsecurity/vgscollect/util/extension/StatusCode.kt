package com.verygoodsecurity.vgscollect.util.extension

private const val UNAUTHORIZED_STATUS_CODE = 401
private const val FORBIDDEN_STATUS_CODE = 403

fun Int.isAccessTokenFailureStatusCode(): Boolean {
    return this == UNAUTHORIZED_STATUS_CODE || this == FORBIDDEN_STATUS_CODE
}
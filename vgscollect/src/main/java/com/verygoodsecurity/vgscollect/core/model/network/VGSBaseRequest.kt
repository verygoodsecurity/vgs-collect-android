package com.verygoodsecurity.vgscollect.core.model.network

import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.api.VGSHttpBodyFormat
import com.verygoodsecurity.vgscollect.core.model.VGSCollectFieldNameMappingPolicy
import com.verygoodsecurity.vgscollect.core.model.network.tokenization.VGSCreateAliasesRequest
import com.verygoodsecurity.vgscollect.core.model.network.tokenization.VGSTokenizationRequest

internal const val DEFAULT_CONNECTION_TIME_OUT = 60_000L

/**
 * Base class for building configuration requests in VGS Collect SDK .
 *
 * @property method HTTP method
 * @property path path for a request
 * @property customHeader The headers to save for request.
 * @property customData The Map to save for request.
 * @property fieldsIgnore contains true if need to skip data from input fields.
 * @property fileIgnore contains true if need to skip files.
 * @property routeId
 */
abstract class VGSBaseRequest {
    abstract val method: HTTPMethod
    abstract val path: String
    abstract val customHeader: Map<String, String>
    abstract val customData: Map<String, Any>
    abstract val fieldsIgnore: Boolean
    abstract val fileIgnore: Boolean
    abstract val format: VGSHttpBodyFormat
    abstract val fieldNameMappingPolicy: VGSCollectFieldNameMappingPolicy
    abstract val requestTimeoutInterval: Long
    abstract val routeId: String?

    val isTokenization: Boolean = this is VGSTokenizationRequest || this is VGSCreateAliasesRequest
}
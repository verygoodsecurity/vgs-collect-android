package com.verygoodsecurity.vgscollect.core.model.network

import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.api.VGSHttpBodyFormat
import com.verygoodsecurity.vgscollect.core.model.VGSCollectFieldNameMappingPolicy

/**
 * Base class for building configuration requests in VGS Collect SDK .
 *
 * @param method HTTP method
 * @param path path for a request
 * @param customHeader The headers to save for request.
 * @param customData The Map to save for request.
 * @param fieldsIgnore contains true if need to skip data from input fields.
 * @param fileIgnore contains true if need to skip files.
 * @param routeId
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
    internal abstract val requiresTokenization: Boolean
}
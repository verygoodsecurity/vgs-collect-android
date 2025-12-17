package com.verygoodsecurity.vgscollect.core.model.network

import com.verygoodsecurity.sdk.analytics.model.VGSAnalyticsUpstream
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.api.VGSHttpBodyFormat
import com.verygoodsecurity.vgscollect.core.model.VGSCollectFieldNameMappingPolicy

/**
 * Base class for VGS Collect requests.
 *
 * @property method The HTTP method to use.
 * @property path The path to the API endpoint.
 * @property customHeader The custom headers to send with the request.
 * @property customData The custom data to send with the request.
 * @property fieldsIgnore Whether to ignore the fields in the request.
 * @property fileIgnore Whether to ignore the files in the request.
 * @property format The format of the request body.
 * @property fieldNameMappingPolicy The policy for mapping field names.
 * @property requestTimeoutInterval The request timeout interval in milliseconds.
 * @property routeId The route id for submitting data.
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
    internal abstract val upstream: VGSAnalyticsUpstream
}
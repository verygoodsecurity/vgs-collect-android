package com.verygoodsecurity.vgscollect.core.model

/**
 * Defines fieldName mapping policy.
 */
enum class VGSCollectFieldNameMappingPolicy {

    /**
     * Map fieldName to JSON. Deep nested key format is supported.
     * VGSCollect supports this format by default.
     */
    NESTED_JSON,

    /**
     * Map fieldName to JSON and Arrays if index is specified. Also merge extra data array with
     * collect array data at the same nested level if possible.
     */
    NESTED_JSON_WITH_ARRAYS_MERGE,

    /**
     * Map fieldName to JSON and Arrays if index is specified. Completely overwrite extra data array with collect array data.
     */
    NESTED_JSON_WITH_ARRAYS_OVERWRITE
}
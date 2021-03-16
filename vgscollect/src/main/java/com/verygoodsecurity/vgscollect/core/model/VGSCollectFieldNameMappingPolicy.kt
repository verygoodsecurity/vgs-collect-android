package com.verygoodsecurity.vgscollect.core.model

/**
 * Defines fieldName mapping.
 */
enum class VGSCollectFieldNameMappingPolicy {

    /**
     * Map fieldName to JSON. Deep nested key format is supported.
     * VGSCollect supports this format by default.
     */
    NESTED_JSON,

    /**
     * Map fieldName to JSON and Arrays if index is specified.
     */
    NESTED_JSON_WITH_ARRAYS_MERGE,

    /**
     * Map fieldName to JSON and Arrays if index is specified.
     */
    NESTED_JSON_WITH_ARRAYS_OVERWRITE
}
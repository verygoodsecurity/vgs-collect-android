package com.verygoodsecurity.vgscollect.core.model

/**
 * Defines fieldName mapping policy.
 */
enum class VGSCollectFieldNameMappingPolicy constructor(internal val analyticsName: String) {

    /**
     * Map fieldName to JSON. Deep nested key format is supported. Completely overwrite extra data.
     * Arrays are not supported.
     * VGSCollect supports this format by default.
     *
     * When you need to send data in a specific JSON structure you may do it by adding . notation to fieldName-string.
     * Each . in a fieldName represents a new level of nesting.
     * New field name string could be set into app:fieldName or setFieldName method.
     */
    NESTED_JSON("nested_json"),

    /**
     * Map fieldName to JSON. A value uses in JSON without any changes before submitting.
     * Completely overwrite extra data.
     */
    FLAT_JSON("flat_json"),

    /**
     * Map fieldName to JSON with arrays if index is specified. Also merge extra data array with
     * collect array data at the same nested level if possible.
     */
    NESTED_JSON_WITH_ARRAYS_MERGE("nested_json_array_merge"),

    /**
     * Map fieldName to JSON with arrays if index is specified. Completely overwrite extra data
     * array with collect array data.
     */
    NESTED_JSON_WITH_ARRAYS_OVERWRITE("nested_json_array_overwrite")
}
package com.verygoodsecurity.vgscollect.core.model

/**
 * Defines fieldName mapping.
 */
sealed class VGSCollectFieldNameMappingPolicy {

    /**
     * Map fieldName to JSON. Deep nested key format is supported.
     * VGSCollect supports this format by default.
     */
    object NestedJson : VGSCollectFieldNameMappingPolicy()

    /**
     * Map fieldName to JSON and Arrays if index is specified.
     *
     * @constructor primary constructor.
     * @param arrayMergePolicy - defines arrays merge policy
     */
    data class NestedJsonWithArray constructor(val arrayMergePolicy: VGSArrayMergePolicy): VGSCollectFieldNameMappingPolicy()
}

/**
 * Defines data merge policy on submit.
 */
enum class VGSArrayMergePolicy {

    /** Overwrite array. */
    OVERWRITE,

    /** Deep merge arrays. */
    MERGE
}
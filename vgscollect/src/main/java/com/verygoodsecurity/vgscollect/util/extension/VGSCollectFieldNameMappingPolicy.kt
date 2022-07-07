package com.verygoodsecurity.vgscollect.util.extension

import com.verygoodsecurity.vgscollect.core.model.VGSCollectFieldNameMappingPolicy

internal fun VGSCollectFieldNameMappingPolicy.isArraysIgnored(): Boolean {
    return this == VGSCollectFieldNameMappingPolicy.FLAT_JSON
}

internal fun VGSCollectFieldNameMappingPolicy.allowParseArrays(): Boolean {
    return when (this) {
        VGSCollectFieldNameMappingPolicy.FLAT_JSON -> false
        VGSCollectFieldNameMappingPolicy.NESTED_JSON -> false
        VGSCollectFieldNameMappingPolicy.NESTED_JSON_WITH_ARRAYS_MERGE -> true
        VGSCollectFieldNameMappingPolicy.NESTED_JSON_WITH_ARRAYS_OVERWRITE -> true
    }
}
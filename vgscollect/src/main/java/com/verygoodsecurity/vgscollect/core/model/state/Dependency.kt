package com.verygoodsecurity.vgscollect.core.model.state

import com.verygoodsecurity.vgscollect.core.storage.DependencyType

/** @suppress */
data class Dependency(val dependencyType: DependencyType = DependencyType.RANGE, val value:Any) {

    companion object {

        fun text(value: Any) = Dependency(DependencyType.TEXT, value)

        fun length(value: Any) = Dependency(DependencyType.LENGTH, value)

        fun range(value: Any) = Dependency(DependencyType.RANGE, value)

        fun cardType(value: Any) = Dependency(DependencyType.CARD_TYPE, value)
    }
}
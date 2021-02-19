package com.verygoodsecurity.vgscollect.core.model.state

import com.verygoodsecurity.vgscollect.core.storage.DependencyType

/** @suppress */
data class Dependency(val dependencyType: DependencyType, val value: Any) {

    companion object {

        fun text(value: Any) = Dependency(DependencyType.TEXT, value)

        fun length(length: Int) = Dependency(DependencyType.LENGTH, length)

        fun card(card: FieldContent.CardNumberContent) = Dependency(DependencyType.CARD, card)
    }
}
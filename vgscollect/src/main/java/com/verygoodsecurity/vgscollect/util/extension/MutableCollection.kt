package com.verygoodsecurity.vgscollect.util.extension

import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState

/** @suppress */
internal fun MutableCollection<Pair<String, String>>.merge(
    collection:MutableCollection<Pair<String, String>>
) :MutableCollection<Pair<String, String>> {

    val keys = this.unzip().first
    val m = collection.filter { state->
        keys.contains(state.first).not()
    }
    this.addAll(m)
    return this
}

/** @suppress */
internal fun MutableCollection<VGSFieldState>.toAssociatedList()
        : MutableCollection<Pair<String, String>> {
    return this.filter { state ->
        !state.fieldName.isNullOrBlank() && !state.content?.data.isNullOrEmpty()
    }.map { state->
        val content = state.content!!
        val name = state.fieldName!!

        val data = when(content) {
            is FieldContent.CardNumberContent -> content.rawData?:content.data!!
            is FieldContent.SSNContent -> content.rawData?:content.data!!
            is FieldContent.CreditCardExpDateContent -> content.rawData?:content.data!!
            else -> content.data!!
        }

        name to data
    }.toMutableList()
}



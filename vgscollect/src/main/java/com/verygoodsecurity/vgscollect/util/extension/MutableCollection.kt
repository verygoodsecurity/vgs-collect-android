package com.verygoodsecurity.vgscollect.util.extension

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


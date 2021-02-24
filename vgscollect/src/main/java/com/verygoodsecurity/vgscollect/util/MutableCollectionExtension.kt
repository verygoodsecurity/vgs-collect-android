package com.verygoodsecurity.vgscollect.util

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










/** @suppress */
internal fun MutableCollection<Pair<String, String>>.mapUsefulPayloads(
    userData: HashMap<String, Any>? = null
): Map<String,Any>? {

    val map = userData ?: HashMap()

    this.forEach {
        val contentData = it.second
        it.first.split(".").
        filter {
            it.isNotEmpty()
        }.mapStr(map, contentData)
    }

    return map
}

private fun List<String>.mapStr(
    userData: HashMap<String, Any>,
    content: String
):Map<String,Any> {

    var lastChainMap:HashMap<String, Any> = userData
    for (i in 0 until size) {
        val key = this[i]
        if(i == size-1) {
            lastChainMap[key] = content
        } else {
            if(lastChainMap.containsKey(key)) {
                val value = lastChainMap[key]

                when(value) {
                    is Map<*, *> -> {
                        lastChainMap = value as HashMap<String, Any>
                    }
                    is Array<*> -> {
                        val mutL = value.toMutableList()

                        var containtsТestedItems = false
                        if(i+1 <= size-1) {
                            val nextKey = this[i+1]
                            mutL.forEach {
                                if(it != null && it is HashMap<*,*> && it.containsKey(nextKey)) {
                                    containtsТestedItems = true
                                    lastChainMap = it as HashMap<String, Any>
                                }
                            }
                        }
                        if(!containtsТestedItems) {
                            val map = HashMap<String, Any>()
                            mutL.add(map)
                            lastChainMap[key] = mutL
                            lastChainMap = map
                        }

                    }
                    is Collection<*> -> {
                        val mutL = value.toMutableList()

                        var containtsТestedItems = false
                        if(i+1 <= size-1) {
                            val nextKey = this[i+1]
                            mutL.forEach {
                                if(it != null && it is HashMap<*,*> && it.containsKey(nextKey)) {
                                    containtsТestedItems = true
                                    lastChainMap = it as HashMap<String, Any>
                                }
                            }
                        }
                        if(!containtsТestedItems) {
                            val map = HashMap<String, Any>()
                            mutL.add(map)
                            lastChainMap[key] = mutL
                            lastChainMap = map
                        }

                    }
                    else -> {
                        val map = HashMap<String, Any>()
                        lastChainMap[key] = map
                        lastChainMap = map
                    }
                }
            } else {
                lastChainMap = key.maps(lastChainMap)
            }
        }
    }
    return userData
}

private fun String.maps(m:HashMap<String,Any>):HashMap<String,Any>  {
    val map = HashMap<String, Any>()

    m[this] = map

    return map
}




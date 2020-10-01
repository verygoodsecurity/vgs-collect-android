package com.verygoodsecurity.vgscollect.core.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

/** @suppress */
@Parcelize
internal class VGSHashMapWrapper<K,V>(
    private val hashMap: @RawValue HashMap<K, V> = HashMap()
) : Parcelable {

    internal fun put(key:K, value:V) {
        hashMap[key] = value
    }

    internal fun get(key: K)= hashMap[key]

    internal fun mapOf() = hashMap

    internal fun isEmpty():Boolean = hashMap.isEmpty()
}
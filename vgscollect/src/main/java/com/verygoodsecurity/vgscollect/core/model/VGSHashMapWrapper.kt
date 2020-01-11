package com.verygoodsecurity.vgscollect.core.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
internal class VGSHashMapWrapper<K,V>(
    private val hashMap: @RawValue HashMap<K, V> = HashMap()
) : Parcelable {

    internal fun put(key:K, value:V) {
        hashMap[key] = value
    }

    internal fun get(key: K)= hashMap[key]

    internal fun mapOf() = hashMap

//    companion object {
//        internal const val PAY_CARD = "vgs_pay_card"
//
//        fun mapToPayCard(bndl:Bundle?):PayCard {
//            val number = bndl?.getString(BaseConfirmationActivity.CARD_NUMBER)
//            bndl?.remove(BaseConfirmationActivity.CARD_NUMBER)
//            val cvv = bndl?.getString(BaseConfirmationActivity.CARD_CVV)
//            val holder = bndl?.getString(BaseConfirmationActivity.CARD_HOLDER)
//            val expDate = bndl?.getString(BaseConfirmationActivity.CARD_EXP_DATE)
//
//            return PayCard(
//                number,
//                holder,
//                cvv,
//                expDate)
//        }
//    }
}
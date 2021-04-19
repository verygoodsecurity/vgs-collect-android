package com.verygoodsecurity.vgscollect.app.mapper

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/** Provides mapping between views and data. */
@Parcelize
class VGSDataMapper private constructor(
    private val cardNumberFieldName: String?,
    private val cardHolderFieldName: String?,
    private val expirationDateFieldName: String?,
    private val cvcFieldName: String?,
): Parcelable {

    fun map(card: VGSCard): Map<String, String> {
        val result = mutableMapOf<String, String>()
        cardNumberFieldName?.let { result[it] = card.number }
        cardHolderFieldName?.let { result[it] = card.holderName }
        expirationDateFieldName?.let { result[it] = card.expirationDate }
        cvcFieldName?.let { result[it] = card.cvc }
        return result
    }

    /** VGSNFCDataMapper builder class */
    class Builder {

        private var cardNumberFieldName: String? = null
        private var cardHolderFieldName: String? = null
        private var expirationDateFieldName: String? = null
        private var cvcFieldName: String? = null

        /**
         * Specify field name of card number view that should be updated.
         */
        fun setCardNumber(fieldName: String?) = this.apply {
            cardNumberFieldName = fieldName
        }

        /**
         * Specify field name of card holder name view that should be updated.
         */
        fun setCardHolder(fieldName: String?) = this.apply {
            cardHolderFieldName = fieldName
        }

        /**
         * Specify field name of card expiration date view that should be updated.
         */
        fun setExpirationDate(fieldName: String?) = this.apply {
            expirationDateFieldName = fieldName
        }

        /**
         * Specify field name of card verification code view that should be updated.
         */
        fun setCVC(fieldName: String?) = this.apply {
            cvcFieldName = fieldName
        }

        /** Create instance of [VGSDataMapper] */
        fun build() = VGSDataMapper(
            cardNumberFieldName,
            cardHolderFieldName,
            expirationDateFieldName,
            cvcFieldName
        )
    }
}
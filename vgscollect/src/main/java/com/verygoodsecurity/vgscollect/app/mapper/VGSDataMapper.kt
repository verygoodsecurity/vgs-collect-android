package com.verygoodsecurity.vgscollect.app.mapper

import android.os.Parcelable
import com.verygoodsecurity.vgscollect.util.extension.putIfNotNull
import kotlinx.parcelize.Parcelize

/** Provides mapping between views and data. */
@Parcelize
class VGSDataMapper internal constructor(
    private val cardNumberFieldName: String?,
    private val cardHolderFieldName: String?,
    private val expirationDateFieldName: String?,
    private val cvcFieldName: String?,
    private val postalCodeFieldName: String?,
) : Parcelable {

    fun map(card: VGSCard): Map<String, Any?> {
        val result = mutableMapOf<String, Any?>()
        result.putIfNotNull(cardNumberFieldName, card.number)
        result.putIfNotNull(cardHolderFieldName, card.holderName)
        result.putIfNotNull(expirationDateFieldName, card.expirationDate)
        result.putIfNotNull(cvcFieldName, card.cvc)
        result.putIfNotNull(postalCodeFieldName, card.postalCode)
        return result
    }

    /** VGSNFCDataMapper builder class */
    class Builder {

        private var cardNumberFieldName: String? = null
        private var cardHolderFieldName: String? = null
        private var expirationDateFieldName: String? = null
        private var cvcFieldName: String? = null
        private var postalCodeFieldName: String? = null

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

        /**
         * Specify field name of postal code view that should be updated.
         */
        fun setPostalCode(fieldName: String?) = this.apply {
            postalCodeFieldName = fieldName
        }

        /** Create instance of [VGSDataMapper] */
        fun build() = VGSDataMapper(
            cardNumberFieldName,
            cardHolderFieldName,
            expirationDateFieldName,
            cvcFieldName,
            postalCodeFieldName
        )
    }
}
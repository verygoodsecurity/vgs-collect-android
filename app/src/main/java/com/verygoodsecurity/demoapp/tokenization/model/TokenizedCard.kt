package com.verygoodsecurity.demoapp.tokenization.model

import com.google.gson.annotations.SerializedName

data class TokenizedCard constructor(
    @SerializedName("card.number") val number: String,
    @SerializedName("card.name") val holder: String,
    @SerializedName("card.expiry") val expiry: String,
    @SerializedName("card.cvc") val cvc: String,
)
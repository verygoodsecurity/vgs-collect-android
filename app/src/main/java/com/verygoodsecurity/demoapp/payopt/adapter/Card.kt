package com.verygoodsecurity.demoapp.payopt.adapter

internal data class Card constructor(
    val finId: String,
    val holderName: String,
    val last4: String,
    val expiryMonth: Int,
    val expiryYear: Int,
    val brand: String,
)
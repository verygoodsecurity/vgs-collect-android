package com.verygoodsecurity.vgscollect.app.mapper

class VGSCard constructor(
    internal val number: String?,
    internal val holderName: String?,
    internal val expirationDate: Any?,
    internal val cvc: String?,
    internal val postalCode: String?,
)
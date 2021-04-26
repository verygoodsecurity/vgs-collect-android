package com.verygoodsecurity.api.nfc.core.model

data class ApplicationFileLocator(
    var sfi: Int = 0,
    var firstRecord: Int = 0,
    var lastRecord: Int = 0,
    var isOfflineAuthentication: Boolean = false
)

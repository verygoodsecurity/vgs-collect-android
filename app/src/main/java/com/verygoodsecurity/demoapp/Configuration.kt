package com.verygoodsecurity.demoapp

import com.verygoodsecurity.vgscollect.core.Environment

object Configuration {
    val tennantId: String = BuildConfig.VGS_TENNANT_ID
    val environment: Environment = Environment.SANDBOX
    val endpoint: String = BuildConfig.VGS_ENDPOINT
}
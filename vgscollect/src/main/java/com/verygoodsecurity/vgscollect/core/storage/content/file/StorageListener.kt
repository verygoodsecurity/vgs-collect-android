package com.verygoodsecurity.vgscollect.core.storage.content.file

import com.verygoodsecurity.sdk.analytics.model.VGSAnalyticsUpstream
import com.verygoodsecurity.vgscollect.core.model.network.VGSError

internal interface StorageListener {

    fun onStorageError(
        error: VGSError,
        upstream: VGSAnalyticsUpstream = VGSAnalyticsUpstream.CUSTOM,
        vararg params: String?
    )
}
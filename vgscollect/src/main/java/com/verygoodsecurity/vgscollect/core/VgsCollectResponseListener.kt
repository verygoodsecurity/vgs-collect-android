package com.verygoodsecurity.vgscollect.core

import com.verygoodsecurity.vgscollect.core.model.VGSResponse

/**
 * Interface definition for a callback to be invoked when VGSCollect receive response from Server.
 *
 * @version 1.0.0
 */
interface VgsCollectResponseListener {

    /**
     * Called when response is detected
     *
     * @param response Response
     */
    fun onResponse(response: VGSResponse?)
}
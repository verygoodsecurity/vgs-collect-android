package com.verygoodsecurity.vgscollect.core

import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse

/**
 * A listener for VGS Collect network responses.
 */
interface VgsCollectResponseListener {

    /**
     * Called when a VGS Collect network request receives a response.
     *
     * @param response The response from the VGS server.
     */
    fun onResponse(response: VGSResponse?)
}
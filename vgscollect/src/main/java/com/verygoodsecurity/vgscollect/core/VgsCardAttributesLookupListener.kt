package com.verygoodsecurity.vgscollect.core

/**
 * Listener for receiving the result of a VGS card attributes lookup operation.
 */
interface VgsCardAttributesLookupListener {

    /**
     * Called when the lookup operation has started.
     */
    fun onStart()


    /**
     * Called when the lookup operation completes successfully.
     *
     * @param code request status code.
     * @param body the retrieved card attributes.
     */
    fun onSuccess(code: Int, body: String)

    /**
     * Called when the lookup operation fails.
     *
     * @param code the error code describing the failure.
     * @param body an optional response body.
     * @param message an optional human-readable error message.
     */
    fun onFailure(code: Int, body: String?, message: String?)
}
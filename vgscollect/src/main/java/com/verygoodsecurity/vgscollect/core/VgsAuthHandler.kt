package com.verygoodsecurity.vgscollect.core

/**
 * Provides an authentication token for VGSCollect operations.
 *
 * This interface allows VGSCollect to request a token asynchronously without
 * blocking the calling thread. Implementations are responsible for fetching
 * or generating a valid token and returning it via the provided callbacks.
 *
 * @see VGSCollect
 */
fun interface VgsAuthHandler {

    /**
     * Requests an authentication token.
     *
     * This method must return immediately and perform any long-running or
     * asynchronous work internally (for example, a network request).
     *
     * @param onComplete Called when a valid authentication token is available.
     */
    fun requestToken(onComplete: (token: String) -> Unit)
}
package com.verygoodsecurity.vgscollect.core

/**
 *
 * Define type of Vault for VGSCollect to communicate with.
 * Please note, sensitive data cannot be used in the Sandbox environment.
 * Therefore, it’s a risk-free and stress-free environment to test out VGS.
 * You can easily modify your settings or even wipe the whole vault if you’d like.
 *
 * @param rawValue Unique identifier.
 *
 * @version 1.1.0
 */
enum class Environment(val rawValue: String) {

    /**
     *  Local Sandbox Environment using for local testing using locally running
     *  VGS server @see [VGS-satellite](https://github.com/verygoodsecurity/vgs-satellite)
     */
    LOCAL_SANDBOX("local_sandbox"),

    /**
     *  Sandbox Environment using sandbox Test Vault
     */
    SANDBOX("sandbox"),

    /**
     *  Live Environment using Live Vault
     */
    LIVE("live")
}

internal fun String.isLive(): Boolean {
    return this.contains("live")
}

internal fun String.isSandbox(): Boolean {
    return this.contains("sandbox")
}

internal fun String.isLocalSandbox() = this == Environment.LOCAL_SANDBOX.rawValue
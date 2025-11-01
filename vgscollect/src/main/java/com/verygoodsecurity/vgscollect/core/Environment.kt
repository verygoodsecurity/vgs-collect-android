package com.verygoodsecurity.vgscollect.core

/**
 * The VGS Collect environment.
 *
 * @param rawValue The environment as a string.
 */
enum class Environment(val rawValue:String) {

    /**
     *  Sandbox Environment using sandbox Test Vault
     */
    SANDBOX("sandbox"),

    /**
     *  Live Environment using Live Vault
     */
    LIVE("live")
}

internal fun String.isLive():Boolean {
    return this.contains("live")
}

internal fun String.isSandbox():Boolean {
    return this.contains("sandbox")
}
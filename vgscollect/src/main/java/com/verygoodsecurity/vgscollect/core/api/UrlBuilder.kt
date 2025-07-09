package com.verygoodsecurity.vgscollect.core.api

import com.verygoodsecurity.vgscollect.VGSCollectLogger
import com.verygoodsecurity.vgscollect.core.Environment

internal object UrlBuilder {

    fun buildCardManagerUrl(env: String): String {
        if (env.isBlank() || !env.isEnvironmentValid()) {
            VGSCollectLogger.warn(message = "Environment is not valid")
            return ""
        }
        val scheme = "https://"
        val divider = "."
        val domain = "vgsapi.com"

        val builder = StringBuilder(scheme)

        if (env == Environment.SANDBOX.rawValue) {
            builder.append(env)
            builder.append(divider)
        }

        builder.append(domain)

        return builder.toString()
    }

    fun buildCollectUrl(vault: String?, env: String): String {
        return when {
            vault.isNullOrBlank() || !vault.isIdValid() -> {
                VGSCollectLogger.warn(message = "Vault ID is not valid")
                return ""
            }

            env.isEmpty() || !env.isEnvironmentValid() -> {
                VGSCollectLogger.warn(message = "Environment is not valid")
                return ""
            }

            else -> {
                val domain = "verygoodproxy.com"
                val divider = "."
                val scheme = "https://"

                val builder = StringBuilder(scheme)
                    .append(vault).append(divider)
                    .append(env).append(divider)
                    .append(domain)

                return builder.toString()
            }
        }
    }

    internal fun buildCnameUrl(host: String, vault: String): String {
        return String.format(
            "https://js.verygoodvault.com/collect-configs/%s__%s.txt",
            host,
            vault
        )
    }
}
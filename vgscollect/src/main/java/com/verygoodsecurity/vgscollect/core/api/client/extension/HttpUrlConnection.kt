package com.verygoodsecurity.vgscollect.core.api.client.extension

import com.verygoodsecurity.vgscollect.core.HTTPMethod
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSocketFactory


@Throws(ClassCastException::class, MalformedURLException::class)
internal fun String.openConnection(): HttpURLConnection {
    return (URL(this).openConnection() as HttpURLConnection).apply {
        useCaches = false
        allowUserInteraction = false
        instanceFollowRedirects = false
    }
}

internal fun HttpURLConnection.callTimeout(timeout: Long): HttpURLConnection {
    connectTimeout = timeout.toInt()
    return this
}

internal fun HttpURLConnection.readTimeout(timeout: Long): HttpURLConnection {
    readTimeout = timeout.toInt()
    return this
}

internal fun HttpURLConnection.setSSLSocketFactory(factory: SSLSocketFactory): HttpURLConnection {
    (this as? HttpsURLConnection)?.sslSocketFactory = factory
    return this
}

internal fun HttpURLConnection.setCacheEnabled(enabled: Boolean): HttpURLConnection {
    useCaches = enabled
    return this
}

internal fun HttpURLConnection.setIsUserInteractionEnabled(enabled: Boolean): HttpURLConnection {
    allowUserInteraction = enabled
    return this
}

internal fun HttpURLConnection.setInstanceFollowRedirectEnabled(enabled: Boolean): HttpURLConnection {
    instanceFollowRedirects = enabled
    return this
}

internal fun HttpURLConnection.addHeader(key: String, value: String): HttpURLConnection {
    setRequestProperty(key, value)
    return this
}

internal fun HttpURLConnection.addHeaders(headers: Map<String, String>?): HttpURLConnection {
    headers?.forEach {
        setRequestProperty(it.key, it.value)
    }
    return this
}

internal fun HttpURLConnection.setMethod(method: HTTPMethod): HttpURLConnection {
    requestMethod = method.name
    return this
}

internal fun HttpURLConnection.getHeaders(): Map<String, String> {
    val result = mutableMapOf<String, String>()
    var index = 0
    while (true) {
        val key = getHeaderFieldKey(index)
        val value = getHeaderField(index)
        if (key == null || value == null) {
            return result
        }
        result[key] = value
        index++
    }
}
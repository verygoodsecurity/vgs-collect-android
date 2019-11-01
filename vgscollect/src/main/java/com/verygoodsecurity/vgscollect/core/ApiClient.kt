package com.verygoodsecurity.vgscollect.core

import android.net.Uri
import com.verygoodsecurity.vgscollect.core.model.SimpleResponse
import java.net.HttpURLConnection.HTTP_OK
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

internal class ApiClient {
    companion object {
        private const val CHARSET = "ISO-8859-1"
        private const val POST = "POST"

        private const val CONTENT_LENGHT = "Content-Length"
        private const val CONTENT_TYPE = "Content-type"
        private const val APPLICATION_JSON = "application/json"

        private const val AGENT = "vgs-client"
        private const val TEMPORARY_STR_AGENT = "source=androidSDK&medium=vgs-collect&content=1.0"


    }

    fun callPost(urlTo: String, content: String?): SimpleResponse {
        //    System.setProperty("http.keepAlive", "false");

        var conn: HttpURLConnection? = null
        var response: SimpleResponse?
//        try {
            val url = URL("$urlTo/post")
            conn = url.openConnection() as HttpURLConnection
            conn.useCaches = false
            conn.allowUserInteraction = false
//            conn.connectTimeout = CONNECTION_TIME_OUT
//            conn.readTimeout = CONNECTION_TIME_OUT
            conn.instanceFollowRedirects = false
            conn.requestMethod = POST
            // ... and get rid of this
            //        conn.setRequestProperty("Connection", "close");
            conn.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON)
            conn.setRequestProperty(AGENT, TEMPORARY_STR_AGENT)

            val length = content?.byteInputStream(Charset.forName(CHARSET))
            conn.setRequestProperty(CONTENT_LENGHT, length.toString())
            conn.doOutput = true

            val os = conn.outputStream
            val writer = BufferedWriter(OutputStreamWriter(os, CHARSET))
            writer.write(content)
            writer.flush()
            writer.close()
            os.close()

            val responseCode = conn.responseCode
            var responseStr:String? = null
            if (responseCode == HTTP_OK) {
                responseStr = conn.inputStream?.bufferedReader()?.use { it.readText() }

            }

            response =
                SimpleResponse(responseStr, responseCode)

//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

        conn.disconnect()

        return response
    }
}

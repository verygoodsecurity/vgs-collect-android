package com.verygoodsecurity.vgscollect.core.storage.content.file

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.util.HashMap

internal class Base64Cipher(context: Context):VgsFileCipher {
    private val contentResolver = (context as Activity).contentResolver
    private var submitCode = -1L
    private var fieldName = ""

    fun save(fieldName:String):Long {
        if (submitCode == -1L) {
            this.fieldName = fieldName
            submitCode = System.currentTimeMillis()
        }
        return submitCode
    }

    fun retrieve(map: HashMap<String, Any?>):Pair<String, String>? {
        val uri = map[submitCode.toString()]?.toString()
        val pair = uri?.run {
            uri to fieldName
        }
        reset()
        return pair
    }

    private fun reset() {
        submitCode = -1L
        fieldName = ""
    }

    private fun getFileBase64(attachment: ByteArray?): String {
        return attachment?.run {
            Base64.encodeToString(attachment, Base64.NO_WRAP)
        }?:""
    }

    private fun getFile(fileUri: Uri): ByteArray? {
        var ret: ByteArray? = null

        contentResolver?.openInputStream(fileUri)?.use { inputStream->
            val outputStream = ByteArrayOutputStream()

            var nextByte = inputStream.read()
            while (nextByte != -1) {
                outputStream.write(nextByte)
                nextByte = inputStream.read()
            }

            ret = outputStream.toByteArray()
            outputStream.close()
        }

        return ret
    }

    override fun getBase64(file: Uri): String {
        val ff = getFile(file)
        return getFileBase64(ff)
    }
}
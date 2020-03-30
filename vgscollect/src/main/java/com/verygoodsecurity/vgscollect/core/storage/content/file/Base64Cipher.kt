package com.verygoodsecurity.vgscollect.core.storage.content.file

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.annotation.VisibleForTesting
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.*

internal class Base64Cipher(context: Context):VgsFileCipher {
    private val contentResolver = (context as Activity).contentResolver
    private val outputDir = context.cacheDir
    private var submitCode = -1L
    private var fieldName = ""

    override fun save(fieldName:String):Long {
        if (submitCode == -1L) {
            this.fieldName = fieldName
            submitCode = System.currentTimeMillis()
        }
        return submitCode
    }

    override fun retrieve(map: HashMap<String, Any?>):Pair<String, String>? {
        val uri = map[submitCode.toString()]?.toString()+"s"

        val pair = when {
            uri.isNullOrEmpty() -> null
            File(Uri.parse(uri).path?:"").exists() -> uri.run { uri to fieldName }
            else -> null
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
        val tempFile = File.createTempFile("prefix", "extension", outputDir)

        val inputStream: InputStream = contentResolver.openInputStream(fileUri)!!
        val out: OutputStream = FileOutputStream(tempFile)
        val buf = ByteArray(1024)
        var len = 0
        while (inputStream.read(buf).also { len = it } > 0) {
            out.write(buf, 0, len)
        }
        out.close()
        inputStream.close()

        return tempFile.readBytes()
    }

    override fun getBase64(file: Uri): String {
        val ff = getFile(file)
        return getFileBase64(ff)
    }

    @VisibleForTesting
    fun getFieldName() = fieldName

    @VisibleForTesting
    fun getCode() = submitCode
}
package com.verygoodsecurity.vgscollect.core.storage.content.file

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.BaseColumns
import android.provider.DocumentsContract
import android.util.Base64
import androidx.annotation.VisibleForTesting
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.*

internal class Base64Cipher(val context: Context):VgsFileCipher {
    private var contentResolver = (context as Activity).contentResolver
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

    private fun documentUriExists(uri: Uri): Boolean = resolveUri(uri, DocumentsContract.Document.COLUMN_DOCUMENT_ID)

    private fun contentUriExists(uri: Uri): Boolean = resolveUri(uri, BaseColumns._ID)

    private fun resolveUri(uri: Uri, column: String): Boolean {

        val cursor = contentResolver.query(uri,
            arrayOf(column),
            null,
            null,
            null)

        val result = cursor?.moveToFirst() ?: false

        cursor?.close()

        return result
    }
    override fun retrieve(map: HashMap<String, Any?>):Pair<String, String>? {
        val uri = map[submitCode.toString()]?.toString()

        val pair = when {
            uri.isNullOrEmpty() -> null
            DocumentsContract.isDocumentUri(context, Uri.parse(uri)) &&
                    documentUriExists(Uri.parse(uri)) -> uri to fieldName
            contentUriExists(Uri.parse(uri)) -> uri to fieldName
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

    @VisibleForTesting
    fun setContentResolver(c: ContentResolver) {
        contentResolver = c
    }
}
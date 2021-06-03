package com.verygoodsecurity.vgscollect.core.storage.content.file

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.BaseColumns
import android.provider.DocumentsContract
import android.util.Base64
import androidx.annotation.VisibleForTesting
import com.verygoodsecurity.vgscollect.util.extension.toBase64String
import com.verygoodsecurity.vgscollect.util.extension.useIfMemoryEnough
import java.util.*

internal class Base64Cipher(val context: Context):VgsFileCipher {
    private var contentResolver = (context as Activity).contentResolver
    private var submitCode = -1L
    private var fieldName = ""

    override fun save(fieldName: String): Long {
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
        val uri = map[submitCode.toString()]?.toString().run {
            if(isNullOrEmpty()) {
                null
            } else {
                Uri.parse(this)
            }
        }

        val pair = when {
            uri == null -> null
            DocumentsContract.isDocumentUri(context, uri) && documentUriExists(uri) -> fieldName to uri.toString()
            contentUriExists(uri) -> fieldName to uri.toString()
            else -> null
        }
        reset()

        return pair
    }

    private fun reset() {
        submitCode = -1L
        fieldName = ""
    }

    override fun getBase64(uri: Uri, maxSize: Long): String {
        return readBytes(uri, maxSize)?.toBase64String(Base64.NO_WRAP) ?: ""
    }

    private fun readBytes(uri: Uri, maxSize: Long): ByteArray? {
        contentResolver.openInputStream(uri)?.useIfMemoryEnough(maxSize) {
            return it.readBytes()
        }
        return null
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
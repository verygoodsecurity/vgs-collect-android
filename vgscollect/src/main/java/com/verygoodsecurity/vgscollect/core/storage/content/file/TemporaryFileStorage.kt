package com.verygoodsecurity.vgscollect.core.storage.content.file

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import com.verygoodsecurity.vgscollect.app.FilePickerActivity
import com.verygoodsecurity.vgscollect.util.parseFile
import java.util.HashMap

internal class TemporaryFileStorage(
    private val context: Context
) : VGSContentProvider, FileStorage {

    companion object {
        internal const val REQUEST_CODE = 0x3712
    }

    private var cipher:VgsFileCipher = Base64Cipher(context)

    private val store = mutableMapOf<FileData, Pair<String, String>>()

    override fun detachAll() {
        store.clear()
    }

    override fun attachFile(fieldName : String) {
        val tag = cipher.save(fieldName)

        val mRequestFileIntent = Intent(context, FilePickerActivity::class.java)

        val bndl = Bundle().apply {
            putString(FilePickerActivity.TAG, tag.toString())
        }
        mRequestFileIntent.putExtras(bndl)
        (context as Activity).startActivityForResult(mRequestFileIntent,
            REQUEST_CODE
        )
    }

    override fun getAttachedFiles():List<FileData> = store.keys.toList()

    override fun detachFile(file: FileData) {
        store.remove(file)
    }

    override fun dispatch(map: HashMap<String, Any?>) {
        cipher.retrieve(map)?.let {
            addItem(it.second, it.first)
        }
    }

    override fun clear() {
        store.clear()
    }

    override fun addItem(fieldName: String, uriStr: String) {
        val fileInfo = Uri.parse(uriStr).parseFile(context, fieldName)
        fileInfo?.let {
            store.clear()
            store[fileInfo] = Pair(it.fieldName, uriStr)
        }
    }

    override fun getItems(): MutableCollection<String> {
        return store.values.unzip().first.toMutableList()
    }

    override fun getAssociatedList(): MutableCollection<Pair<String, String>> {
        return store.values.map {
            val uri = Uri.parse(it.second)
            val fileBase64 = cipher.getBase64(uri)
            it.first to fileBase64
        }.toMutableList()
    }

    @VisibleForTesting
    fun setCipher(c:VgsFileCipher) {
        cipher = c
    }
}
package com.verygoodsecurity.vgscollect.core.storage.content.file

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import com.verygoodsecurity.vgscollect.app.FilePickerActivity
import com.verygoodsecurity.vgscollect.core.model.network.VGSError
import com.verygoodsecurity.vgscollect.core.model.state.FileState
import com.verygoodsecurity.vgscollect.util.extension.parseFile
import java.util.HashMap

internal class TemporaryFileStorage(
    private val context: Context,
    private val errorListener: StorageErrorListener? = null
) : VGSFileProvider, FileStorage {

    companion object {
        internal const val REQUEST_CODE = 0x3712
    }

    private var cipher:VgsFileCipher = Base64Cipher(context)

    private val store = mutableMapOf<FileState, Pair<String, String>>()

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

    override fun getAttachedFiles():List<FileState> = store.keys.toList()

    override fun detachFile(file: FileState) {
        store.remove(file)
    }

    override fun dispatch(map: HashMap<String, Any?>) {
        val fileInfo = cipher.retrieve(map)

        if(fileInfo == null) {
            errorListener?.onStorageError(VGSError.FILE_NOT_FOUND)
        } else {
            addItem(fileInfo.second, fileInfo.first)
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
package com.verygoodsecurity.vgscollect.core.storage.content.file

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.LruCache
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

    private var cipher: VgsFileCipher = Base64Cipher(context)

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal val memoryCache:LruCache<String, String> by lazy {
        val totalMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = totalMemory / 8
        object : LruCache<String, String>(cacheSize) {}
    }

    private val store = mutableMapOf<String, FileState>()

    override fun detachAll() {
        store.clear()
        memoryCache.evictAll()
    }

    override fun resize(size: Int) {
        memoryCache.resize(size)
    }

    override fun attachFile(fieldName: String) {
        val tag = cipher.save(fieldName)

        val mRequestFileIntent = Intent(context, FilePickerActivity::class.java)

        val bndl = Bundle().apply {
            putString(FilePickerActivity.TAG, tag.toString())
        }
        mRequestFileIntent.putExtras(bndl)
        (context as Activity).startActivityForResult(
            mRequestFileIntent,
            REQUEST_CODE
        )
    }

    override fun getAttachedFiles(): List<FileState> = store.values.toList()

    override fun detachFile(file: FileState) {
        store.remove(file.fieldName)
        memoryCache.remove(file.fieldName)
    }

    override fun dispatch(map: HashMap<String, Any?>) {
        val fileInfo = cipher.retrieve(map)

        fileInfo?.second?.let {
            val base64Content = cipher.getBase64(Uri.parse(it))
            memoryCache.put(fileInfo.first, base64Content)
        }

        if (fileInfo == null) {
            errorListener?.onStorageError(VGSError.FILE_NOT_FOUND)
        } else {
            addItem(fileInfo.first, fileInfo.second)
        }
    }

    override fun clear() {
        store.clear()
        memoryCache.evictAll()
    }

    override fun remove(key: String) {
        memoryCache.remove(key)
        store.remove(key)
    }

    override fun addItem(fieldName: String, uriStr: String) {
        val fileInfo = Uri.parse(uriStr).parseFile(context, fieldName)
        fileInfo?.let {
            store.clear()
            store[it.fieldName] = fileInfo
        }
    }

    override fun getItems(): MutableCollection<String> {
        return store.keys
    }

    override fun getAssociatedList(): MutableCollection<Pair<String, String>> {
        return store.keys.map {
            it to memoryCache.get(it)
        }.toMutableList()
    }

    @VisibleForTesting
    fun setCipher(c: VgsFileCipher) {
        cipher = c
    }
}
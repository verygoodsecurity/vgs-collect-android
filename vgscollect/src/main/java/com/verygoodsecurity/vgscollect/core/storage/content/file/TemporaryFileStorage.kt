package com.verygoodsecurity.vgscollect.core.storage.content.file

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import androidx.core.net.toUri
import com.verygoodsecurity.vgscollect.app.FilePickerActivity
import com.verygoodsecurity.vgscollect.core.model.network.VGSError
import com.verygoodsecurity.vgscollect.core.model.state.FileState
import com.verygoodsecurity.vgscollect.util.extension.NotEnoughMemoryException
import com.verygoodsecurity.vgscollect.util.extension.queryOptional
import java.util.*

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
internal class TemporaryFileStorage(
    private val context: Context,
    private val errorListener: StorageErrorListener? = null
) : VGSFileProvider, FileStorage {

    private var cipher: VgsFileCipher = Base64Cipher(context)
    private val fileInfoStore = mutableMapOf<String, FileState>()
    private var encodedFileMaxSize: Long = Runtime.getRuntime().maxMemory() / 8
    private var encodedFile: String? = null

    //region VGSFileProvider
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun resize(size: Int) {
        encodedFileMaxSize = size.toLong()
    }

    override fun attachFile(fieldName: String) {
        (context as? Activity)?.startActivityForResult(
            createFilePickerIntent(fieldName),
            REQUEST_CODE
        ) ?: errorListener?.onStorageError(VGSError.NOT_ACTIVITY_CONTEXT)
    }

    override fun getAttachedFiles(): List<FileState> = fileInfoStore.values.toList()

    override fun detachAll() {
        fileInfoStore.clear()
        encodedFile = null
    }

    override fun detachFile(file: FileState) {
        fileInfoStore.remove(file.fieldName)
        encodedFile = null
    }
    //endregion

    //region FileStorage
    override fun getAssociatedList(): MutableCollection<Pair<String, String>> {
        return fileInfoStore.keys.map {
            it to (encodedFile ?: "")
        }.toMutableList()
    }

    override fun dispatch(map: HashMap<String, Any?>) {
        val fileInfo = cipher.retrieve(map)
        if (fileInfo == null) {
            errorListener?.onStorageError(VGSError.FILE_NOT_FOUND)
            return
        }
        try {
            encodedFile = cipher.getBase64(Uri.parse(fileInfo.second), encodedFileMaxSize)
            addItem(fileInfo.first, fileInfo.second)
        } catch (e: NotEnoughMemoryException) {
            errorListener?.onStorageError(VGSError.FILE_SIZE_OVER_LIMIT)
        }
    }
    //endregion

    //region VgsStore
    override fun clear() {
        fileInfoStore.clear()
        encodedFile = null
    }

    override fun remove(id: String) {
        fileInfoStore.remove(id)
        encodedFile = null
    }

    override fun addItem(fieldName: String, uri: String) {
        val fileState = queryFileState(uri.toUri(), fieldName)
        fileInfoStore.clear()
        fileInfoStore[fileState.fieldName] = fileState
    }

    override fun getItems(): MutableCollection<String> = fileInfoStore.keys
    //endregion

    private fun createFilePickerIntent(fieldName: String): Intent {
        return Intent(context, FilePickerActivity::class.java).apply {
            putExtras(Bundle().apply {
                putString(FilePickerActivity.TAG, cipher.save(fieldName).toString())
            })
        }
    }

    private fun queryFileState(uri: Uri, fieldName: String): FileState {
        var name = ""
        var size = 0L
        context.contentResolver.queryOptional(uri)?.use {
            if (it.moveToFirst()) {
                name = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                size = it.getLong(it.getColumnIndex(OpenableColumns.SIZE))
            }
        }
        return FileState(size, name, context.contentResolver.getType(uri), fieldName)
    }

    @VisibleForTesting
    fun setCipher(c: VgsFileCipher) {
        cipher = c
    }

    companion object {

        internal const val REQUEST_CODE = 0x3712
    }
}
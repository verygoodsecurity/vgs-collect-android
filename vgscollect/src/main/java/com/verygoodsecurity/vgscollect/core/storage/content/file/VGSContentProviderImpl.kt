package com.verygoodsecurity.vgscollect.core.storage.content.file

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import com.verygoodsecurity.vgscollect.app.FilePickerActivity
import com.verygoodsecurity.vgscollect.core.model.VGSHashMapWrapper
import java.io.*

internal class VGSContentProviderImpl(private val context: Context) : VGSContentProvider,
    FileStorage {

    companion object {
        internal const val REQUEST_CODE = 0x3712
    }

    private val cipher = F(context)

    private val store = mutableMapOf<FileData, String>()

    override fun detachAll() {
        store.clear()
    }

    override fun attachFile() {
        val mRequestFileIntent = Intent(context, FilePickerActivity::class.java)

        val bndl = Bundle().apply {
            putString(FilePickerActivity.TAG, cipher.getTimeTag().toString())
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

    override fun getFileCipher(): VgsFileCipher {
        return cipher
    }

    override fun clear() {}

    override fun addItem(id: Int, uriStr: String) {
        val fileInfo = Uri.parse(uriStr).parseFile(context)
        fileInfo?.let { store[fileInfo] = uriStr }

        Log.e("test", "file: ${fileInfo?.name}, ${fileInfo?.size} ${fileInfo?.mimeType}")
        Log.e("test", "uri: $uriStr")
    }

    override fun getItems(): MutableCollection<String> = store.values

    class F(context: Context): VgsFileCipher {
        private val contentResolver = (context as Activity).contentResolver
        private var submitCode = -1L

        fun getTimeTag():Long {
            if (submitCode == -1L) {
                submitCode = System.currentTimeMillis()
            }
            return submitCode
        }

        fun getFileBase64(attachment: ByteArray?): String {
            return attachment?.run {
                Base64.encodeToString(attachment, Base64.NO_WRAP)
            }?:""
        }

        @SuppressLint("NewApi")
        fun getFile(fileUri: Uri): ByteArray? {
            var ret: ByteArray? = null
//            val tempFile = File.createTempFile(
//                "splitName[0]",
//                "jpg"
//            )
//            contentResolver.takePersistableUriPermission(
//                fileUri,
//                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
//            )
    val tempFile = File.createTempFile(
        "splitName[0]",
        "jpg"
    )

    val inputStream: InputStream = contentResolver.openInputStream(fileUri!!)!!
    val out: OutputStream = FileOutputStream(tempFile)
    val buf = ByteArray(1024)
    var len = 0
    while (inputStream.read(buf).also { len = it } > 0) {
        out.write(buf, 0, len)
    }
    out.close()
    inputStream.close()

            contentResolver?.openInputStream(fileUri)?.use { inputStream->
                val outputStream = ByteArrayOutputStream()

                var nextByte = inputStream.read()
                while (nextByte != -1) {
                    outputStream.write(nextByte)
                    nextByte = inputStream.read()
                }

                ret = outputStream.toByteArray()
                outputStream.close()
//                FileOutputStream(tempFile).use { outputStream->
//                    val buf = ByteArray(1024)
//                    var len = 0
//                    while (inputStream.read(buf).also { len = it } > 0) {
//                        outputStream.write(buf, 0, len)
//                    }
//                }
            }

//            val inputStream: InputStream = contentResolver.openInputStream(fileUri)

            return tempFile.readBytes()
        }

        override fun retrieveActivityResult(map: VGSHashMapWrapper<String, Any?>?): String? {
            val entity = map?.get(submitCode.toString()).toString()
            submitCode = -1
            return entity
        }

        override fun getBase64(file: Uri): String {
            val ff = getFile(file)
            return getFileBase64(ff)
        }
    }
}
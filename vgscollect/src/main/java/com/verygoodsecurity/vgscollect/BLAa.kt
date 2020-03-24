package com.verygoodsecurity.vgscollect

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import android.util.Log
import java.io.*

object BLAa {

    fun bla2(fileUri: Uri, context: Context) {
        var ret: ByteArray? = null
        val mimeType: String? = fileUri?.let { returnUri ->
            context.contentResolver.getType(returnUri)
        }
        var name:String = ""
        var sizeIndexStr:String = ""
        fileUri?.let { returnUri ->
            context.contentResolver.query(returnUri, null, null, null, null)
        }?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            cursor.moveToFirst()
            name = cursor.getString(nameIndex)
            sizeIndexStr = cursor.getLong(sizeIndex).toString()
        }


        context.contentResolver?.openInputStream(fileUri)?.use { inputStream->
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

        val file = bla1(fileUri, context)
        val base64Str = Base64.encodeToString(file.readBytes(), Base64.NO_WRAP)
        Log.e("test", "file: $fileUri, $mimeType $name $sizeIndexStr $file")
        Log.e("test", "base64Str: $base64Str")

    }

    fun bla(fileUri: Uri?, context: Context) {
        val mimeType: String? = fileUri?.let { returnUri ->
            context.contentResolver.getType(returnUri)
        }
        var name:String = ""
        var sizeIndexStr:String = ""
        fileUri?.let { returnUri ->
            context.contentResolver.query(returnUri, null, null, null, null)
        }?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            cursor.moveToFirst()
            name = cursor.getString(nameIndex)
            sizeIndexStr = cursor.getLong(sizeIndex).toString()
        }


        val file = bla1(fileUri, context)
        val base64Str = Base64.encodeToString(file.readBytes(), Base64.NO_WRAP)
        Log.e("test", "file: $fileUri, $mimeType $name $sizeIndexStr $file")
        Log.e("test", "base64Str: $base64Str")
    }

    fun bla1(fileUri: Uri?, context: Context): File {
        val tempFile = File.createTempFile(
            "splitName[0]",
            "jpg"
        )

        val inputStream: InputStream = context.contentResolver.openInputStream(fileUri!!)!!
        val out: OutputStream = FileOutputStream(tempFile)
        val buf = ByteArray(1024)
        var len = 0
        while (inputStream.read(buf).also { len = it } > 0) {
            out.write(buf, 0, len)
        }
        out.close()
        inputStream.close()

        return tempFile
    }
}
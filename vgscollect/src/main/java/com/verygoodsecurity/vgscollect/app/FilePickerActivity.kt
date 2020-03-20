package com.verygoodsecurity.vgscollect.app

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import java.io.File

class FilePickerActivity :BaseTransmitActivity() {

    companion object {
        private const val PICK_FILE_REQUEST_CODE = 0x107

        const val TAG = "vgs_f_picker_act_tag"
    }

    private var configKey:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupConfigKey()

        selectFile()
    }

    private fun setupConfigKey() {
        configKey = intent.extras?.getString(TAG)?.run { this }
    }

    private fun selectFile() {
//        var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
        var chooseFile = Intent(Intent.ACTION_OPEN_DOCUMENT)
        chooseFile.type = "*/*"
        chooseFile = Intent.createChooser(chooseFile, "Choose a file")
        startActivityForResult(chooseFile, PICK_FILE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_FILE_REQUEST_CODE) {
            configKey?.let { key->
                mapData(key, data?.data.toString())
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
        finish()
    }


    private fun handleOnActivityResult() {
//        if(requestCode == 789) {
//            val fileUri = data?.data
//            val mimeType: String? = fileUri?.let { returnUri ->
//                contentResolver.getType(returnUri)
//            }
//            var name:String = ""
//            var sizeIndexStr:String = ""
//            fileUri?.let { returnUri ->
//                contentResolver.query(returnUri, null, null, null, null)
//            }?.use { cursor ->
//                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
//                cursor.moveToFirst()
//                name = cursor.getString(nameIndex)
//                sizeIndexStr = cursor.getLong(sizeIndex).toString()
//            }
//
//
//            val file = bla(fileUri!!)
//            val base64Str = convertToBase64(file)
//            Log.e("test", "file: $fileUri, $mimeType $name $sizeIndexStr $file")
//            Log.e("test", "base64Str: $base64Str")
//
//
//            val arr = decodefromBase64(base64Str)
////            saveReceivedImage(arr!!, arr.size)
//        }
    }


    fun convertToBase64(attachment: File): String {
        return Base64.encodeToString(attachment.readBytes(), Base64.NO_WRAP)
    }

    fun bla(fileUri: Uri): File {
        val tempFile = File.createTempFile(
            "splitName[0]",
            "jpg"
        )

//        val inputStream: InputStream = contentResolver.openInputStream(fileUri)
//        val out: OutputStream = FileOutputStream(tempFile)
//        val buf = ByteArray(1024)
//        var len = 0
//        while (inputStream.read(buf).also { len = it } > 0) {
//            out.write(buf, 0, len)
//        }
//        out.close()
//        inputStream.close()

        return tempFile
    }

    fun decodefromBase64(attachment: String): ByteArray? {
        return Base64.decode(attachment, Base64.NO_WRAP)
    }

    private fun saveReceivedImage(
        imageByteArray: ByteArray,
        numberOfBytes: Int
    ) {
        val bmp = BitmapFactory.decodeByteArray(imageByteArray, 0, numberOfBytes)
//        val image: ImageView = findViewById<View>(R.id.imageView1) as ImageView

//        image.setImageBitmap(
//            Bitmap.createScaledBitmap(
//                bmp, image.getWidth(),
//                image.getHeight(), false
//            )
//        )
    }

}
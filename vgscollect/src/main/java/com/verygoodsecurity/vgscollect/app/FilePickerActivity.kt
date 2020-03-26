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
}
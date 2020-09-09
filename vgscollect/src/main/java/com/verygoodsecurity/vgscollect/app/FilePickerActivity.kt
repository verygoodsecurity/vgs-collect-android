package com.verygoodsecurity.vgscollect.app

import android.content.Intent
import android.os.Bundle

/**
 * The Activity class that use when need to attach some file to VGSCollect.
 */
internal class FilePickerActivity :BaseTransmitActivity() {

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
        configureInternalSettings(data)
        if(requestCode == PICK_FILE_REQUEST_CODE) {
            configKey?.let { key->
                mapData(key, data?.data.toString())
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
        finish()
    }

    private fun configureInternalSettings(data: Intent?) {
        mapData(RESULT_TYPE, ATTACH)
        if(data?.data?.toString().isNullOrEmpty()) {
            mapData(RESULT_STATUS, Status.CLOSE.raw)
        } else {
            mapData(RESULT_STATUS, Status.SUCCESS.raw)
        }
    }
}
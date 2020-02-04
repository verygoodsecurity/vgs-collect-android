package com.verygoodsecurity.vgscollect.app

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.verygoodsecurity.vgscollect.core.model.VGSHashMapWrapper

abstract class BaseTransmitActivity:AppCompatActivity() {
    companion object {
        const val RESULT_DATA = "vgs_result_settings"
    }

    private val storage = VGSHashMapWrapper<String, String>()

    protected val resultIntent = Intent()

    protected fun mapData(key: String?, value: String?) {
        if(!key.isNullOrEmpty() && !value.isNullOrEmpty()) {
            storage.put(key, value)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        resultIntent.putExtra(RESULT_DATA, storage)
        if(resultCode != Activity.RESULT_CANCELED) {
            setResult(Activity.RESULT_OK, resultIntent)
        } else {
            setResult(Activity.RESULT_CANCELED)
        }
    }
}
package com.verygoodsecurity.vgscollect.app

import android.app.Activity
import android.content.Intent
import android.os.Parcelable
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.verygoodsecurity.vgscollect.core.model.VGSHashMapWrapper

/**
 * Base class for activities that use the AppCompatActivity as a parent.
 * You can use the activity when need to send some data to VGSCollect from external api.
 *
 * @version 1.0.2
 */
abstract class BaseTransmitActivity:AppCompatActivity() {
    companion object {
        const val RESULT_DATA = "vgs_result_settings"
    }

    private val storage = VGSHashMapWrapper<String, String>()

    /**
     * The Intent that this activity will return to its caller.
     */
    protected val resultIntent = Intent()

    /**
     * Associates the specified value with the specified key in the HashMap.
     * Is used for storing data that need to be sent as a result.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     */
    protected fun mapData(key: String?, value: String?) {
        if(!key.isNullOrEmpty() && !value.isNullOrEmpty()) {
            storage.put(key, value)
        }
    }

    override fun onStart() {
        super.onStart()
        Log.e("test", "B onStart")
    }

    override fun onStop() {
        super.onStop()
        Log.e("test", "B onStop")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        resultIntent.data = data?.data//fixme test string

        resultIntent.putExtra(RESULT_DATA, storage)
        if(resultCode != Activity.RESULT_CANCELED) {
            setResult(Activity.RESULT_OK, resultIntent)
        } else {
            setResult(Activity.RESULT_CANCELED)
        }
        intent.putExtra(RESULT_DATA, storage)
    }
}
package com.verygoodsecurity.vgscollect.app

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.verygoodsecurity.vgscollect.core.model.VGSHashMapWrapper

/**
 * Base class for activities that use the AppCompatActivity as a parent.
 * You can use the activity when need to send some data to VGSCollect from external api.
 *
 * @since 1.0.2
 */
abstract class BaseTransmitActivity:AppCompatActivity() {
    companion object {
        const val RESULT_DATA = "vgs_result_settings"
        const val RESULT_STATUS = "com.vgs.collect.status"
        const val RESULT_TYPE = "com.vgs.collect.type"
        const val RESULT_NAME = "com.vgs.collect.sw"
        const val RESULT_ID = "com.vgs.collect.id"

        const val SCAN = "com.vgs.scan_type"
        const val ATTACH = "com.vgs.attach_f_type"
    }

    enum class Status(val raw:String) {
        SUCCESS("Ok"),
        FAILED("Failed"),
        CLOSE("Cancel")
    }

    private val storage = VGSHashMapWrapper<String, Any>()

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
    protected fun mapData(key: String?, value: Any?) {
        if(!key.isNullOrEmpty() && value != null) {
            storage.put(key, value)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        resultIntent.putExtra(RESULT_DATA, storage)
        if(resultCode != Activity.RESULT_CANCELED) {
            setResult(Activity.RESULT_OK, resultIntent)
        } else {
            setResult(Activity.RESULT_CANCELED, resultIntent)
        }
    }
}
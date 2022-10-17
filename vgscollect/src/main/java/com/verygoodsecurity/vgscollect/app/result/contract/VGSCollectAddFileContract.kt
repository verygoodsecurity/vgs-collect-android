package com.verygoodsecurity.vgscollect.app.result.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.verygoodsecurity.vgscollect.app.BaseTransmitActivity
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.model.VGSHashMapWrapper
import com.verygoodsecurity.vgscollect.core.storage.content.file.TemporaryFileStorage

/**
 * An VGSCollectAddFileContract helps a developer transmot a user file to VGS Proxy.
 */
class VGSCollectAddFileContract(
    collect: VGSCollect
) : ActivityResultContract<String, Int>() {

    private val storage: TemporaryFileStorage = collect.getFileProvider() as TemporaryFileStorage

    override fun createIntent(context: Context, input: String): Intent {
        return storage.createFilePickerIntent(input, context)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Int {
        if (resultCode == Activity.RESULT_OK) {
            intent?.extras?.getParcelable<VGSHashMapWrapper<String, Any?>>(
                BaseTransmitActivity.RESULT_DATA
            )?.let {
                storage.dispatch(it.mapOf())
            }
        }

        return resultCode
    }

}
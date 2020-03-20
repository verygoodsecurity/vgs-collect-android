package com.verygoodsecurity.vgscollect.core.storage.content.file

import android.net.Uri
import com.verygoodsecurity.vgscollect.core.model.VGSHashMapWrapper

internal interface VgsFileCipher {
    fun retrieveActivityResult(map: VGSHashMapWrapper<String, Any?>?):String?

    fun getBase64(file:Uri):String
}
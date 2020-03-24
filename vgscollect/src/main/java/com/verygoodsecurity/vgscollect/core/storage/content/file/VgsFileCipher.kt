package com.verygoodsecurity.vgscollect.core.storage.content.file

import android.net.Uri

internal interface VgsFileCipher {
    fun getBase64(file:Uri):String
}
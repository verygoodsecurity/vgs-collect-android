package com.verygoodsecurity.vgscollect.core.storage.content.file

import android.net.Uri
import java.util.HashMap

internal interface VgsFileCipher {
    fun save(fieldName:String):Long
    fun retrieve(map: HashMap<String, Any?>):Pair<String, String>?

    fun getBase64(file:Uri):String
}
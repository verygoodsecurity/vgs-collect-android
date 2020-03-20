package com.verygoodsecurity.vgscollect.core.storage.content.file

import com.verygoodsecurity.vgscollect.core.storage.VgsStore
import com.verygoodsecurity.vgscollect.core.storage.content.file.VgsFileCipher

internal interface FileStorage:VgsStore<String> {
    fun getFileCipher(): VgsFileCipher
}
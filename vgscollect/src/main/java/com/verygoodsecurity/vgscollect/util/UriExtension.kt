package com.verygoodsecurity.vgscollect.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.verygoodsecurity.vgscollect.core.model.state.FileState

/** @suppress */
internal fun Uri.parseFile(context: Context, fieldName:String): FileState? {
    val mimeType: String? = this.let { returnUri ->
        context.contentResolver.getType(returnUri)
    }
    var name = ""
    var sizeIndexL = 0L
    this.let { returnUri ->
        context.contentResolver.query(returnUri, null, null, null, null)
    }?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        cursor.moveToFirst()
        name = cursor.getString(nameIndex)
        sizeIndexL = cursor.getLong(sizeIndex)
    }

    return FileState(
        sizeIndexL,
        name,
        mimeType,
        fieldName
    )
}
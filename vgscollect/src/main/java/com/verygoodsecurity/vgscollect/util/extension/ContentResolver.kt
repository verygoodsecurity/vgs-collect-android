package com.verygoodsecurity.vgscollect.util.extension

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.CancellationSignal

/** @suppress */
internal fun ContentResolver.queryOptional(
    uri: Uri,
    projection: Array<String>? = null,
    selection: String? = null,
    selectionArgs: Array<String>? = null,
    sortOrder: String? = null,
    cancellationSignal: CancellationSignal? = null
): Cursor? = this.query(uri, projection, selection, selectionArgs, sortOrder, cancellationSignal)
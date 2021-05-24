package com.verygoodsecurity.vgscollect.util.extension

import android.util.Base64

internal fun ByteArray.toBase64String(flags: Int) = Base64.encodeToString(this, flags)
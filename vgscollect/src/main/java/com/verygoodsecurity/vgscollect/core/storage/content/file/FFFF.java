package com.verygoodsecurity.vgscollect.core.storage.content.file;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

public class FFFF {
    @SuppressWarnings("ResourceType") @TargetApi(Build.VERSION_CODES.KITKAT)
    public static Uri ensureUriPermission(Context context, Intent intent) {
        Uri uri = intent.getData();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final int takeFlags = intent.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
            context.getContentResolver().takePersistableUriPermission(uri, takeFlags);
        }
        return uri;
    }
}

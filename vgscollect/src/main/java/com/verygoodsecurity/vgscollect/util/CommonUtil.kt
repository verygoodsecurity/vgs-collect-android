package com.verygoodsecurity.vgscollect.util

import java.io.File

/** @suppress */
internal object CommonUtil {
    fun isRooted():Boolean {
        val buildTags = android.os.Build.TAGS
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true
        }

        // check if /system/app/Superuser.apk is present
        try {
            val file = File("/system/app/Superuser.apk")
            if (file.exists()) {
                return true
            }
        } catch (e1: Exception) {
            // ignore
        }

        return canExecuteCommand("su")
    }

    private fun canExecuteCommand(command:String):Boolean {
        return try {
            Runtime.getRuntime().exec(command)
            true
        } catch (e: Exception) {
            false
        }
    }
}
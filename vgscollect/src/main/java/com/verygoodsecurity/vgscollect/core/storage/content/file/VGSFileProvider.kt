package com.verygoodsecurity.vgscollect.core.storage.content.file

import android.os.Build
import androidx.annotation.RequiresApi
import com.verygoodsecurity.vgscollect.core.model.state.FileState

/**
 * VGSFileProvider is a part of VGSCollect SDK. The instance provides means for file management inside the SDK.
 * It allows managing files inside SDK by attach/detach/get file general info methods.
 */
interface VGSFileProvider {

    /**
     * Specify the maximum size of the cache for file stored before submit to the Proxy Server.
     *
     * @param cacheSize The new maximum size.
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun resize(cacheSize: Int)

    /**
     * Mentioned below method allows to attach file to the temporary local file storage before
     * its sending to the Server.
     *
     * @param fieldName is a key under which the file for JSON will be saved before sending.
     */
    fun attachFile(fieldName : String)

    /**
     * Method is used to get attached files for reviewing them before their further sending.
     *
     * @return list of [FileState]
     */
    fun getAttachedFiles():List<FileState>

    /**
     * The method is used for detaching all previously attached files.
     */
    fun detachAll()

    /**
     * This method detaches only one separate file.
     *
     * @param [FileState]
     */
    fun detachFile(file: FileState)
}
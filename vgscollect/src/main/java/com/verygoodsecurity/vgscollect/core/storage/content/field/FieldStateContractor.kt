package com.verygoodsecurity.vgscollect.core.storage.content.field

import android.content.Context
import com.verygoodsecurity.vgscollect.core.model.network.VGSError
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.core.storage.StorageContractor
import com.verygoodsecurity.vgscollect.VGSLogger
import com.verygoodsecurity.vgscollect.view.InputFieldView

/** @suppress */
internal class FieldStateContractor(
    private val context: Context
) : StorageContractor<VGSFieldState> {

    override fun checkState(state: VGSFieldState): Boolean {
        return if (state.fieldName?.trim().isNullOrEmpty()) {
            val message = context.getString(VGSError.FIELD_NAME_NOT_SET.messageResId)
            VGSLogger.warn(InputFieldView.TAG, message)
            false
        } else {
            true
        }
    }

}
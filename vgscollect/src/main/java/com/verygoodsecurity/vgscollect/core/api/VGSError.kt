package com.verygoodsecurity.vgscollect.core.api

import com.verygoodsecurity.vgscollect.R

enum class VGSError(val code:Int, val messageResId:Int) {
    INPUT_DATA_REQUIRED(1001,
        R.string.error_field_validation
    ),
    INPUT_DATA_REQUIRED_VALID_ONLY(1002,
        R.string.error_field_validation
    ),
    INPUT_FILE_SIZE_EXCEEDS_THE_LIMIT(1101,
        R.string.error_file_size_validation
    ),
    INPUT_FILE_TYPE_IS_WRONG(1102,
        R.string.error_field_validation
    ),
    INPUT_FILE_TYPE_IS_NOT_SUPPORTED(1103,
        R.string.error_field_validation
    ),
    INPUT_FILE_NOT_FOUND(1103,
        R.string.error_field_validation
    )
}
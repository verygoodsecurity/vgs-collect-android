package com.verygoodsecurity.vgscollect.core.api

import com.verygoodsecurity.vgscollect.R

enum class VGSError(val code:Int, val messageResId:Int) {
    URL_NOT_VALID(1480,
        R.string.error_url_validation
    ),
    NO_INTERNET_PERMISSIONS(1481,
        R.string.error_internet_permission
    ),
    NO_NETWORK_CONNECTIONS(1482,
        R.string.error_internet_connection
    ),
    TIME_OUT(1483,
        R.string.error_time_out
    ),



    INPUT_DATA_NOT_VALID(1001,
        R.string.error_field_validation
    ),
    FILE_SIZE_OVER_LIMIT(1101,
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
package com.verygoodsecurity.vgscollect.core.model

sealed class VGSResponse(val code:Int = -1) {

    class SuccessResponse(val response:Map<String, String>? = null, val successCode:Int = -1):VGSResponse(successCode)

    class ErrorResponse(val localizeMessage:String? = "", val errorCode:Int = -1):VGSResponse(errorCode)

}
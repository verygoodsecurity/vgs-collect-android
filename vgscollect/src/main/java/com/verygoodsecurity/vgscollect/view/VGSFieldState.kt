package com.verygoodsecurity.vgscollect.view

data class VGSFieldState(var isFocusable:Boolean = false,
                         var isRequired:Boolean = true,
                         var type:VGSTextInputType = VGSTextInputType.InfoField,
                         var placeholder:String? = null,
                         var formatPattern:String? = null,
                         var alias:String? = null)  /// Field name - actualy this is key for you JSON wich contains data
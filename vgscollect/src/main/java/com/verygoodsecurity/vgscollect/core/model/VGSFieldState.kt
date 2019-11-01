package com.verygoodsecurity.vgscollect.core.model

import com.verygoodsecurity.vgscollect.view.VGSTextInputType

data class VGSFieldState(var isFocusable:Boolean = false,
                         var isRequired:Boolean = true,
                         var type: VGSTextInputType = VGSTextInputType.InfoField,
                         var placeholder:String? = null,        //just value from @EditText.hint
                         var formatPattern:String? = null,
                         var content:String? = null,
                         var alias:String? = null)  /// Field name - actualy this is key for you JSON wich contains data

package com.verygoodsecurity.vgscollect.widget.compose.core

abstract class BaseFieldState {

    internal abstract val fieldName: String?

    internal abstract val text: String

    internal abstract val isValid: Boolean
}
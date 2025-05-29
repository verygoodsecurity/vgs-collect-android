package com.verygoodsecurity.vgscollect.widget.compose.core

abstract class BaseFieldState {

    abstract val fieldName: String?

    abstract val isValid: Boolean

    internal abstract val text: String
}
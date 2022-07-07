package com.verygoodsecurity.vgscollect

import org.mockito.ArgumentCaptor

object Utils {

    fun <T> capture(argumentCaptor: ArgumentCaptor<T>): T = argumentCaptor.capture()
}
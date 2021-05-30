package com.verygoodsecurity.vgscollect.util.extension

import java.io.InputStream
import kotlin.math.min

internal class NotEnoughMemoryException : Exception()

@Throws(NotEnoughMemoryException::class)
internal inline fun <R> InputStream.useIfMemoryEnough(maxSize: Long, block: (InputStream) -> R): R {
    if (this.available() >= min(Runtime.getRuntime().freeMemory() / 2, maxSize)) {
        throw NotEnoughMemoryException()
    }
    this.use {
        return block.invoke(it)
    }
}
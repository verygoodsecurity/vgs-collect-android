package com.verygoodsecurity.vgscollect.util.extension

import java.io.InputStream

internal class NotEnoughMemoryException : Exception()

@Throws(NotEnoughMemoryException::class)
internal inline fun <R> InputStream.useIfMemoryEnough(block: (InputStream) -> R): R {
    if (this.available() >= Runtime.getRuntime().freeMemory() / 2) {
        throw NotEnoughMemoryException()
    }
    this.use {
        return block.invoke(it)
    }
}
package com.verygoodsecurity.vgscollect.util.extension

import android.util.Log
import java.io.InputStream
import kotlin.math.min

internal class NotEnoughMemoryException : Exception()

@Throws(NotEnoughMemoryException::class)
internal inline fun <R> InputStream.useIfMemoryEnough(maxSize: Long, block: (InputStream) -> R): R {
    val halfOfFreeMemory = Runtime.getRuntime().freeMemory() / 2
    val target = min(halfOfFreeMemory, maxSize)
    Log.d("Test", "useIfMemoryEnough, maxSize = ${maxSize / 1024 / 1024}, halfOfFreeMemory = ${halfOfFreeMemory / 1024 / 1024}, target = ${target / 1024 / 1024}")
    if (this.available() >= target) {
        throw NotEnoughMemoryException()
    }
    this.use {
        return block.invoke(it)
    }
}
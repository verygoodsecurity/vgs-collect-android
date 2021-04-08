package com.verygoodsecurity.vgscollect.util.extension

inline fun <reified T>Array<T>.except(item: T): Array<T> {
    return this.filter { it != item }.toTypedArray()
}
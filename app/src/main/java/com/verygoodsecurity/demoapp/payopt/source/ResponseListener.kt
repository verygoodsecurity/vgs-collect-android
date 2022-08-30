package com.verygoodsecurity.demoapp.payopt.source

interface ResponseListener<T> {

    fun onSuccess(data: T)

    fun onError(message: String)
}
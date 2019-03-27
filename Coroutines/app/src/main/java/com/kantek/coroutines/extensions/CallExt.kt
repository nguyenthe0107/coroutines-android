package com.kantek.coroutines.extensions

import com.kantek.coroutines.app.TokenException
import retrofit2.Call
import java.net.SocketTimeoutException


fun <T> Call<T>.call(): T {
    val response = execute()
    if (!response.isSuccessful) {
        if (response.code() == 500) throw TokenException()
        throw Throwable(response.toString())
    }
    return response.body() ?: throw Throwable("Body null")
}

fun <T> Call<T>.call(function: T.() -> Unit): T {
    return call().apply(function)
}

fun <T> Call<T>.tryCall(shouldBeSuccess: Throwable.() -> Boolean): T? {
    return try {
        call()
    } catch (e: Throwable) {
        if (!shouldBeSuccess(e)) throw e else null
    }
}
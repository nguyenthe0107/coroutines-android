package com.kantek.coroutines.extensions

import com.kantek.coroutines.app.TokenException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


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

suspend fun <T> Call<T>.submit(): T = suspendCoroutine {
    enqueue(object : Callback<T> {
        override fun onFailure(call: Call<T>, t: Throwable) {
            it.resumeWithException(t)
        }

        override fun onResponse(call: Call<T>, response: Response<T>) {
            val value = try {
                if (!response.isSuccessful) {
                    if (response.code() == 500) throw TokenException()
                    throw Throwable(response.toString())
                }
                response.body() ?: throw Throwable("Body null")
            } catch (t: Throwable) {
                it.resumeWithException(t)
                return
            }
            it.resume(value)
        }
    })
}
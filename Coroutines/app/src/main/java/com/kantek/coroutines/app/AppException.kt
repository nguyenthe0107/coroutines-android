package com.kantek.coroutines.app

import android.support.annotation.StringRes

class AlertException(val resource: Int) : Throwable()

class ResourceException(val resource: Int) : Throwable()

class SnackException(val resource: Int) : Throwable()

class TokenException : Throwable()

class UpdateException(throwable: Throwable, private val payload: Any) : Throwable(throwable) {
    fun <T : Any> get() = payload as T
}

fun fail(@StringRes res: Int): Nothing = throw ResourceException(res)

fun fail(text: String): Nothing = throw Throwable(text)

fun alert(@StringRes res: Int): Nothing = throw AlertException(res)

fun snack(@StringRes res: Int): Nothing = throw SnackException(res)
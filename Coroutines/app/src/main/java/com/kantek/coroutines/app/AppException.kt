package com.kantek.coroutines.app

class AlertException(val resource: Int) : Throwable()

class ResourceException(val resource: Int) : Throwable()

class SnackException(val resource: Int) : Throwable()

class TokenException : Throwable()

class UpdateException(throwable: Throwable, private val payload: Any) : Throwable(throwable) {
    fun <T : Any> get() = payload as T
}
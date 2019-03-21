package com.kantek.coroutines.exceptions

class UpdateException(throwable: Throwable, private val payload: Any) : Throwable(throwable) {
    fun <T : Any> get() = payload as T
}
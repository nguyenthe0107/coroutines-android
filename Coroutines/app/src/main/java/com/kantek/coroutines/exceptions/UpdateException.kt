package com.kantek.coroutines.exceptions

class UpdateException(message: String?, private val payload: Any) : Throwable(message) {
    fun <T : Any> get() = payload as T
}
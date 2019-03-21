package com.kantek.coroutines.datasource

import com.kantek.coroutines.exceptions.TokenException
import com.kantek.coroutines.models.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("users/1")
    fun login(): Call<User>

    @GET("users/{id}/posts")
    fun getPosts(@Query("id") id: String): Call<MutableList<Post>>

    @GET("posts/{id}")
    fun getPost(@Path("id") id: String): Call<Post>

    @GET("posts/{id}/comments")
    fun getComments(@Path("id") id: String): Call<MutableList<Comment>>

    @GET("users/{id}/albums")
    fun getAlbums(@Path("id") id: String): Call<MutableList<Album>>

    @GET("albums/{id}/photos")
    fun getPhotos(@Path("id") id: String): Call<MutableList<Photo>>

    @GET("users/{id}/todos")
    fun getTodos(@Path("id") id: String): Call<MutableList<Todo>>

    @PATCH("users/{id}")
    fun updateProfile(@Path("id") id: String, @Body body: Map<String, String>): Call<User>

    @PATCH("todos/{id}")
    fun updateTodo(@Path("id") id: String, @Body body: Map<String, String>): Call<Todo>
}

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

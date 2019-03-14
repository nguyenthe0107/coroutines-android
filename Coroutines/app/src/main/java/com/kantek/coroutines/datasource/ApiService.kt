package com.kantek.coroutines.datasource

import android.util.Log
import com.kantek.coroutines.exceptions.TokenException
import com.kantek.coroutines.models.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("users/1")
    fun login(): Call<User>

    @GET("users/{userId}/posts")
    fun getPosts(@Query("userId") userId: String): Call<MutableList<Post>>

    @GET("posts/{id}")
    fun getPost(@Path("id") id: String): Call<Post>

    @GET("posts/{id}/comments")
    fun getComments(@Path("id") id: String): Call<MutableList<Comment>>

    @GET("users/{userId}/albums")
    fun getAlbums(@Path("userId") id: String): Call<MutableList<Album>>

    @GET("albums/{id}/photos")
    fun getPhotos(@Path("id") id: String): Call<MutableList<Photo>>

    @GET("users/{userId}/todos")
    fun getTodos(@Path("userId") userId: String): Call<MutableList<Todo>>

    @PATCH("users/{userId}")
    fun updateProfile(@Path("userId") id: String, @Body body: Map<String, String>): Call<User>
}

fun <T> Call<T>.call(): T {
    val response = execute()
    if (!response.isSuccessful) {
        if (response.code() == 500) throw TokenException()
        Log.e("CALL_ERROR", response.toString())
        throw Throwable(response.message())
    }
    return response.body() ?: throw Throwable("Body null")
}

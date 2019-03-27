package com.kantek.coroutines.datasource

import com.kantek.coroutines.models.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("users/1")
    fun login(): Call<User>

    @GET("posts")
    fun getPosts(): Call<MutableList<Post>>

    @GET("posts/{id}")
    fun getPost(@Path("id") id: String): Call<Post>

    @GET("comments")
    fun getComments(@Query("postId") id: String): Call<MutableList<Comment>>

    @GET("albums")
    fun getAlbums(): Call<MutableList<Album>>

    @GET("photos")
    fun getPhotos(@Query("albumId") id: String): Call<MutableList<Photo>>

    @GET("todos")
    fun getTodos(): Call<MutableList<Todo>>

    @PATCH("users/{id}")
    fun updateProfile(@Path("id") id: String, @Body body: Map<String, String>): Call<User>

    @PATCH("todos/{id}")
    fun updateTodo(@Path("id") id: Int, @Body body: Map<String, String>): Call<Todo>
}

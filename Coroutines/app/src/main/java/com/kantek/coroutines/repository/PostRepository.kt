package com.kantek.coroutines.repository

import android.support.core.di.Repository
import com.kantek.coroutines.datasource.ApiService
import com.kantek.coroutines.datasource.AppCache
import com.kantek.coroutines.datasource.call
import com.kantek.coroutines.models.Comment
import com.kantek.coroutines.models.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PostRepository(
    private val apiService: ApiService,
    private val appCache: AppCache
) : Repository {
    private val mPosts = hashMapOf<String, MutableList<Post>>()
    private val mComments = hashMapOf<String, MutableList<Comment>>()
    private val mPost = hashMapOf<String, Post>()

    suspend fun getPosts() = withContext(Dispatchers.IO) {
        val userId = appCache.user!!.id
        if (mPosts.containsKey(userId)) return@withContext mPosts[userId]
        apiService.getPosts(userId).call().also {
            mPosts[userId] = it
        }
    }

    suspend fun getPost(id: String) = withContext(Dispatchers.IO) {
        if (mPost.containsKey(id)) return@withContext mPost[id]
        apiService.getPost(id).call().also {
            mPost[id] = it
        }
    }

    suspend fun getComments(id: String) = withContext(Dispatchers.IO) {
        if (mComments.containsKey(id)) return@withContext mComments[id]
        apiService.getComments(id).call().also {
            mComments[id] = it
        }
    }
}

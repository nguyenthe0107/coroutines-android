package com.kantek.coroutines.repository

import android.support.core.di.Repository
import android.support.core.extensions.withIO
import android.support.core.helpers.TemporaryData
import com.kantek.coroutines.datasource.ApiService
import com.kantek.coroutines.datasource.AppCache
import com.kantek.coroutines.extensions.call
import com.kantek.coroutines.models.Comment
import com.kantek.coroutines.models.Post

class PostRepository(
    private val apiService: ApiService,
    private val appCache: AppCache
) : Repository {
    private val mPosts = TemporaryData<String, MutableList<Post>>()
    private val mComments = TemporaryData<String, MutableList<Comment>>()
    private val mPost = TemporaryData<String, Post>()

    suspend fun getPosts() = withIO {
        val userId = appCache.user!!.id
        mPosts.getOrLoad(userId) { apiService.getPosts().call() }
    }

    suspend fun getPost(id: String) = withIO {
        mPost.getOrLoad(id) { apiService.getPost(id).call() }
    }

    suspend fun getComments(id: String) = withIO {
        mComments.getOrLoad(id) { apiService.getComments(id).call() }
    }
}
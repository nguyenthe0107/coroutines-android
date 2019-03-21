package com.kantek.coroutines.repository

import android.support.core.di.Repository
import android.support.core.extensions.withIO
import com.kantek.coroutines.datasource.ApiService
import com.kantek.coroutines.datasource.AppCache
import com.kantek.coroutines.datasource.call
import com.kantek.coroutines.models.Comment
import com.kantek.coroutines.models.Post

class PostRepository(
    private val apiService: ApiService,
    private val appCache: AppCache
) : Repository {
    private val mPosts = hashMapOf<String, MutableList<Post>>()
    private val mComments = hashMapOf<String, MutableList<Comment>>()
    private val mPost = hashMapOf<String, Post>()

    suspend fun getPosts() = withIO {
        val userId = appCache.user!!.id
        if (mPosts.containsKey(userId)) return@withIO mPosts[userId]
        apiService.getPosts(userId).call {
            mPosts[userId] = this
        }
    }

    suspend fun getPost(id: String) = withIO {
        if (mPost.containsKey(id)) return@withIO mPost[id]
        apiService.getPost(id).call {
            mPost[id] = this
        }
    }

    suspend fun getComments(id: String) = withIO {
        if (mComments.containsKey(id)) return@withIO mComments[id]
        apiService.getComments(id).call {
            mComments[id] = this
        }
    }
}

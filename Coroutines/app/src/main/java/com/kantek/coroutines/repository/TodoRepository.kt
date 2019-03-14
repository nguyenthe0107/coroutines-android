package com.kantek.coroutines.repository

import android.support.core.di.Repository
import com.kantek.coroutines.datasource.ApiService
import com.kantek.coroutines.datasource.AppCache
import com.kantek.coroutines.datasource.call
import com.kantek.coroutines.models.Todo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TodoRepository(
    private val apiService: ApiService,
    private val appCache: AppCache
) : Repository {
    private val mTodos = hashMapOf<String, MutableList<Todo>>()

    suspend fun getTodos() = withContext(Dispatchers.IO) {
        val userId = appCache.user!!.id
        if (mTodos.containsKey(userId)) return@withContext mTodos[userId]
        apiService.getTodos(userId).call().also {
            mTodos[userId] = it
        }
    }

}

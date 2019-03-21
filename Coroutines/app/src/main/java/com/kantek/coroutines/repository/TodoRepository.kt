package com.kantek.coroutines.repository

import android.support.core.di.Repository
import android.support.core.extensions.withIO
import android.support.core.helpers.TemporaryData
import com.kantek.coroutines.datasource.ApiService
import com.kantek.coroutines.datasource.AppCache
import com.kantek.coroutines.exceptions.UpdateException
import com.kantek.coroutines.extensions.call
import com.kantek.coroutines.extensions.tryCall
import com.kantek.coroutines.models.Todo

class TodoRepository(
    private val apiService: ApiService,
    private val appCache: AppCache
) : Repository {
    private val mTodos = TemporaryData<String, MutableList<Todo>>()

    suspend fun getTodos() = withIO {
        val userId = appCache.user!!.id
        mTodos.getOrLoad(userId) { apiService.getTodos(userId).call() }
    }

    suspend fun update(todo: Todo, vararg body: Pair<String, String>) = withIO {
        apiService.updateTodo(todo.id, body.toMap()).tryCall {
            throw UpdateException(this, todo)
        }.let { todo copy it!! }
    }

}

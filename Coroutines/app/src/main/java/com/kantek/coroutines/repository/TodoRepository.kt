package com.kantek.coroutines.repository

import android.support.core.di.Repository
import android.support.core.extensions.withIO
import com.kantek.coroutines.datasource.ApiService
import com.kantek.coroutines.datasource.AppCache
import com.kantek.coroutines.datasource.call
import com.kantek.coroutines.datasource.tryCall
import com.kantek.coroutines.exceptions.UpdateException
import com.kantek.coroutines.models.Todo

class TodoRepository(
    private val apiService: ApiService,
    private val appCache: AppCache
) : Repository {
    private val mTodos = hashMapOf<String, MutableList<Todo>>()

    suspend fun getTodos() = withIO {
        val userId = appCache.user!!.id
        if (mTodos.containsKey(userId)) return@withIO mTodos[userId]
        apiService.getTodos(userId).call {
            mTodos[userId] = this
        }
    }

    suspend fun update(todo: Todo, vararg body: Pair<String, String>) = withIO {
        apiService.updateTodo(todo.id, body.toMap()).tryCall {
            throw UpdateException(message, todo)
        }.let {
            todo.update(it!!)
            todo
        }
    }

}

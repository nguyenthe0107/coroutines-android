package com.kantek.coroutines.repository

import android.support.core.di.Repository
import android.support.core.extensions.withIO
import android.support.core.helpers.TemporaryData
import com.kantek.coroutines.app.UpdateException
import com.kantek.coroutines.datasource.ApiService
import com.kantek.coroutines.datasource.AppCache
import com.kantek.coroutines.datasource.AppDatabase
import com.kantek.coroutines.extensions.call
import com.kantek.coroutines.extensions.tryCall
import com.kantek.coroutines.models.Todo
import java.util.concurrent.TimeUnit

class TodoRepository(
    private val apiService: ApiService,
    private val appCache: AppCache,
    private val appDatabase: AppDatabase
) : Repository {
    private val mTodos = object : TemporaryData<String, MutableList<Todo>>(1, TimeUnit.MINUTES) {
        override fun saveToCache(key: String, value: MutableList<Todo>) {
            appDatabase.todoDao().saveAll(value)
        }

        override fun hasContent(key: String) = appDatabase.todoDao().hasUser(key)

        override fun loadFromCache(key: String) = appDatabase.todoDao().gets(key)
    }

    suspend fun getTodos() = withIO {
        val userId = appCache.user!!.id
        mTodos.getOrLoad(userId) { apiService.getTodos().call() }
    }

    suspend fun update(todo: Todo, vararg body: Pair<String, String>) = withIO {
        apiService.updateTodo(todo.id, body.toMap())
            .tryCall { throw UpdateException(this, todo) }
            .let {
                appDatabase.todoDao().save(it!!)
                todo copy it
            }
    }

}

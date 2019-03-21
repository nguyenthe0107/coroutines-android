package com.kantek.coroutines.viewmodel

import android.support.core.base.BaseViewModel
import android.support.core.event.SingleLiveEvent
import android.support.core.extensions.call
import android.support.core.extensions.map
import android.support.core.extensions.submit
import com.kantek.coroutines.R
import com.kantek.coroutines.datasource.AppCache
import com.kantek.coroutines.exceptions.UpdateException
import com.kantek.coroutines.models.Todo
import com.kantek.coroutines.repository.AlbumRepository
import com.kantek.coroutines.repository.PostRepository
import com.kantek.coroutines.repository.TodoRepository
import com.kantek.coroutines.repository.UserRepository

class MainViewModel(
    postRepository: PostRepository,
    todoRepository: TodoRepository,
    albumRepository: AlbumRepository,
    appCache: AppCache,
    userRepository: UserRepository
) : BaseViewModel() {
    val profile = appCache.userLive
    val updateProfile = SingleLiveEvent<Pair<Int, String>>()
    val updateTodo = SingleLiveEvent<Todo>()
    val updateTodoError = SingleLiveEvent<UpdateException>()

    val posts = refresh.map(this) {
        postRepository.getPosts()
    }

    val albums = refresh.map(this) {
        albumRepository.getAlbums()
    }

    val todos = refresh.map(this) {
        todoRepository.getTodos()
    }

    val updateTodoSuccess = updateTodo.map(this, error = updateTodoError) {
        todoRepository.update(it!!, "completed" to (!it.completed).toString())
    }

    init {
        refresh.call()
        updateProfile.map(this, loading = null) {
            val body = when (it!!.first) {
                R.id.txtName -> "name" to it.second
                R.id.txtEmail -> "email" to it.second
                R.id.txtPhone -> "phone" to it.second
                else -> throw Throwable("Not found field")
            }
            userRepository.update(body)
        }.submit(this)
    }
}

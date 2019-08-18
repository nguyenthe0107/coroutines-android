package com.kantek.coroutines.viewmodel

import android.support.core.event.SingleLiveEvent
import com.kantek.coroutines.R
import com.kantek.coroutines.app.AppViewModel
import com.kantek.coroutines.app.UpdateException
import com.kantek.coroutines.datasource.AppCache
import com.kantek.coroutines.datasource.AppEvent
import com.kantek.coroutines.models.Todo
import com.kantek.coroutines.repository.AlbumRepository
import com.kantek.coroutines.repository.PostRepository
import com.kantek.coroutines.repository.TodoRepository
import com.kantek.coroutines.repository.UserRepository

class MainViewModel(
    postRepository: PostRepository,
    todoRepository: TodoRepository,
    albumRepository: AlbumRepository,
    private val userRepository: UserRepository,
    appCache: AppCache,
    appEvent: AppEvent
) : AppViewModel() {
    val profile = appCache.userLive
    val updateTodo = SingleLiveEvent<Todo>()
    val updateTodoError = SingleLiveEvent<UpdateException>()

    val posts = refresh.next {
        postRepository.getPosts()
    }

    val albums = refresh.next {
        albumRepository.getAlbums()
    }

    val todos = refresh.next {
        todoRepository.getTodos()
    }

    val updateTodoSuccess = updateTodo.next {
        todoRepository.update(it!!, "completed" to (!it.completed).toString())
    }

    fun updateProfile(it: Pair<Int, String>) = launch(loading = null) {
        val body = when (it.first) {
            R.id.txtName -> "name" to it.second
            R.id.txtEmail -> "email" to it.second
            R.id.txtPhone -> "phone" to it.second
            else -> error("Field not found Id@${it.first}")
        }
        userRepository.update(body)
    }

    init {
        refresh.addEvent(appEvent.networkChanged, posts, albums, todos)
    }
}

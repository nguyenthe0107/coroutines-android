package com.kantek.coroutines.viewmodel

import android.support.core.base.BaseViewModel
import android.support.core.event.SingleLiveEvent
import android.support.core.extensions.call
import android.support.core.extensions.mapLaunch
import android.support.core.extensions.submit
import com.kantek.coroutines.R
import com.kantek.coroutines.datasource.AppCache
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

    val posts = refresh.mapLaunch(this) {
        postRepository.getPosts()
    }
    val albums = refresh.mapLaunch(this) {
        albumRepository.getAlbums()
    }
    val todos = refresh.mapLaunch(this) {
        todoRepository.getTodos()
    }

    init {
        refresh.call()
        updateProfile.mapLaunch(this, loading = null) {
            val field = when (it!!.first) {
                R.id.txtName -> "name"
                R.id.txtEmail -> "email"
                R.id.txtPhone -> "phone"
                else -> throw Throwable("Not found field")
            }
            userRepository.update(field to it.second)
        }.submit(this)
    }
}

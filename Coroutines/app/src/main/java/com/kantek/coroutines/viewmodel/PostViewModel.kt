package com.kantek.coroutines.viewmodel

import android.support.core.base.BaseViewModel
import android.support.core.event.RequestEvent
import com.kantek.coroutines.datasource.AppEvent
import com.kantek.coroutines.models.Post
import com.kantek.coroutines.repository.PostRepository

class PostViewModel(
    postRepository: PostRepository,
    appEvent: AppEvent
) : BaseViewModel() {
    val post = RequestEvent<Post>(this)

    val comments = post.next {
        postRepository.getComments(it!!.id)
    }

    init {
        post.addEvent(appEvent.networkChanged, comments)
    }
}

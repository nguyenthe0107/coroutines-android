package com.kantek.coroutines.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.support.core.base.BaseViewModel
import android.support.core.extensions.map
import com.kantek.coroutines.models.Post
import com.kantek.coroutines.repository.PostRepository

class PostViewModel(postRepository: PostRepository) : BaseViewModel() {
    val post = MutableLiveData<Post>()

    val comments = post.map(this) {
        postRepository.getComments(it!!.id)
    }

}

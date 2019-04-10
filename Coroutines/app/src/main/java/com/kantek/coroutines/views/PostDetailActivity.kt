package com.kantek.coroutines.views

import android.os.Bundle
import android.support.core.annotations.LayoutId
import android.support.core.extensions.argument
import android.support.core.extensions.asArgument
import android.support.core.extensions.observe
import android.support.core.extensions.open
import android.support.core.functional.Dispatcher
import com.kantek.coroutines.R
import com.kantek.coroutines.app.AppActivity
import com.kantek.coroutines.models.Post
import com.kantek.coroutines.viewmodel.PostViewModel
import com.kantek.coroutines.views.adapters.CommentAdapter
import kotlinx.android.synthetic.main.activity_post_detail.*

@LayoutId(R.layout.activity_post_detail)
class PostDetailActivity : AppActivity<PostViewModel>() {
    companion object {
        fun show(from: Dispatcher, it: Post) {
            from.open(PostDetailActivity::class, it.asArgument())
        }
    }

    private val mPost: Post by argument()
    private lateinit var mAdapter: CommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAdapter = CommentAdapter(recvContent)
        viewModel.post put mPost
        viewModel.post.observe(this) {
            txtTitle.text = it!!.title
            txtBody.text = it.body
        }
        viewModel.comments.observe(this) {
            mAdapter.items = it
        }
    }
}

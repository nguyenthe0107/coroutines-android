package com.kantek.coroutines.views.fragments

import android.os.Bundle
import android.support.core.annotations.LayoutId
import android.support.core.extensions.call
import android.support.core.extensions.observe
import android.view.View
import com.kantek.coroutines.R
import com.kantek.coroutines.app.AppFragment
import com.kantek.coroutines.viewmodel.MainViewModel
import com.kantek.coroutines.views.PostDetailActivity
import com.kantek.coroutines.views.adapters.PostAdapter
import kotlinx.android.synthetic.main.fragment_post.*

@LayoutId(R.layout.fragment_post)
class PostFragment : AppFragment<MainViewModel>() {
    private lateinit var mAdapter: PostAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAdapter = PostAdapter(recvContent)

        viewModel.posts.observe(this) {
            mAdapter.items = it
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewRefresh.setOnRefreshListener {
            viewModel.refresh.call()
            viewRefresh.isRefreshing = false
        }
        mAdapter.onItemClickListener = {
            PostDetailActivity.show(this, it)
        }
    }
}

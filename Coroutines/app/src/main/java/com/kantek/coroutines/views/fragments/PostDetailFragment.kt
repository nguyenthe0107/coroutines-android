package com.kantek.coroutines.views.fragments

import android.os.Bundle
import android.support.core.annotations.LayoutId
import android.support.core.extensions.get
import android.support.core.extensions.observe
import android.support.design.widget.MenuHostFragment
import android.view.View
import com.kantek.coroutines.R
import com.kantek.coroutines.app.AppFragment
import com.kantek.coroutines.viewmodel.PostViewModel
import com.kantek.coroutines.views.adapters.CommentAdapter
import kotlinx.android.synthetic.main.fragment_post_detail.*

@LayoutId(R.layout.fragment_post_detail)
class PostDetailFragment : AppFragment<PostViewModel>() {
    private lateinit var mAdapter: CommentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAdapter = CommentAdapter(recvContent)

        viewModel.post.observe(this) {
            txtTitle.text = it?.title
            txtBody.text = it?.body
        }

        viewModel.comments.observe(this) {
            mAdapter.items = it
        }
        mAdapter.onItemClickListener = {
            MenuHostFragment.findNavController(this)!!
                .navigate(R.id.commentFragment)
        }
    }

    override fun onNewArguments(args: Bundle) {
        viewModel.post.value = args.get()!!
    }

    override fun onBackPressed(): Boolean {
        return MenuHostFragment.findNavController(this)!!.navigateUp()
    }
}

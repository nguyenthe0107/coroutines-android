package com.kantek.coroutines.views.fragments

import android.os.Bundle
import android.support.core.annotations.LayoutId
import android.support.core.extensions.asArgument
import android.support.core.extensions.observe
import android.support.core.extensions.toBundle
import android.support.design.widget.MenuHostFragment
import android.view.View
import com.kantek.coroutines.R
import com.kantek.coroutines.app.AppFragment
import com.kantek.coroutines.viewmodel.MainViewModel
import com.kantek.coroutines.views.AlbumDetailActivity
import com.kantek.coroutines.views.adapters.AlbumAdapter
import kotlinx.android.synthetic.main.fragment_album.*

@LayoutId(R.layout.fragment_album)
class AlbumFragment : AppFragment<MainViewModel>() {
    private lateinit var mAdapter: AlbumAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAdapter = AlbumAdapter(recvContent)
        viewModel.albums.observe(this) {
            mAdapter.items = it
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mAdapter.onItemClickListener = { AlbumDetailActivity.show(this, it) }
        viewRefresh.setOnRefreshListener {
            viewModel.refresh.call()
            viewRefresh.isRefreshing = false
        }
    }
}
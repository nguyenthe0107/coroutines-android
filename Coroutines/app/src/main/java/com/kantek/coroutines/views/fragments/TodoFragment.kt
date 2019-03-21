package com.kantek.coroutines.views.fragments

import android.os.Bundle
import android.support.core.annotations.LayoutId
import android.support.core.extensions.call
import android.support.core.extensions.observe
import android.view.View
import com.kantek.coroutines.R
import com.kantek.coroutines.app.AppFragment
import com.kantek.coroutines.models.Todo
import com.kantek.coroutines.viewmodel.MainViewModel
import com.kantek.coroutines.views.adapters.TodoAdapter
import kotlinx.android.synthetic.main.fragment_todo.*

@LayoutId(R.layout.fragment_todo)
class TodoFragment : AppFragment<MainViewModel>() {
    private lateinit var mAdapter: TodoAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAdapter = TodoAdapter(recvContent)
        viewModel.todos.observe(this) {
            mAdapter.items = it
        }
        viewModel.updateTodoError.observe(this) {
            toast("${it!!.get<Todo>().title} can not be updated")
        }
        viewModel.updateTodoSuccess.observe(this) {
            mAdapter.notifyItemChanged(it!!)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewRefresh.setOnRefreshListener {
            viewModel.refresh.call()
            viewRefresh.isRefreshing = false
        }
        mAdapter.onItemClickListener = {
            viewModel.updateTodo.value = it
        }
    }
}
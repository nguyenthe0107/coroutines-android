package com.kantek.coroutines.views.adapters

import android.support.core.base.RecyclerAdapter
import android.support.core.base.RecyclerHolder
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.kantek.coroutines.R
import com.kantek.coroutines.models.Todo
import kotlinx.android.synthetic.main.item_view_todo.view.*

class TodoAdapter(view: RecyclerView) : RecyclerAdapter<Todo>(view) {
    var onItemClickListener: ((Todo) -> Unit)? = null

    fun notifyItemChanged(todo: Todo) {
        val index = items!!.indexOf(todo)
        notifyItemChanged(index)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int) = object :
        RecyclerHolder<Todo>(p0, R.layout.item_view_todo) {
        override fun bind(item: Todo) {
            super.bind(item)
            itemView.apply {
                checkTodo.isChecked = item.completed
                checkTodo.text = item.title
                checkTodo.setOnClickListener { onItemClickListener?.invoke(item) }
            }
        }
    }

}

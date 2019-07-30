package com.kantek.coroutines.views.adapters

import android.support.core.base.RecyclerAdapter
import android.support.core.base.RecyclerHolder
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.kantek.coroutines.R
import com.kantek.coroutines.models.Post
import kotlinx.android.synthetic.main.item_view_post.view.*

class PostAdapter(view: androidx.recyclerview.widget.RecyclerView) : RecyclerAdapter<Post>(view) {
    var onItemClickListener: ((Post) -> Unit)? = null

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int) = object :
        RecyclerHolder<Post>(p0, R.layout.item_view_post) {
        override fun bind(item: Post) {
            super.bind(item)
            itemView.apply {
                txtTitle.text = item.title
                txtBody.text = item.body
                setOnClickListener { onItemClickListener?.invoke(item) }
            }
        }
    }
}

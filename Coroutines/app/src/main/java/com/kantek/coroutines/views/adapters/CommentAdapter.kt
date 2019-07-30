package com.kantek.coroutines.views.adapters

import android.support.core.base.RecyclerAdapter
import android.support.core.base.RecyclerHolder
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.kantek.coroutines.R
import com.kantek.coroutines.models.Comment
import kotlinx.android.synthetic.main.item_view_comment.view.*

class CommentAdapter(view: androidx.recyclerview.widget.RecyclerView) : RecyclerAdapter<Comment>(view) {
    var onItemClickListener: ((Comment) -> Unit)? = null

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int) = object :
        RecyclerHolder<Comment>(p0, R.layout.item_view_comment) {
        init {
            itemView.setOnClickListener { onItemClickListener?.invoke(item!!) }
        }

        override fun bind(item: Comment) {
            super.bind(item)
            itemView.apply {
                txtName.text = item.name
                txtEmail.text = item.email
                txtComment.text = item.comment
            }
        }
    }

}

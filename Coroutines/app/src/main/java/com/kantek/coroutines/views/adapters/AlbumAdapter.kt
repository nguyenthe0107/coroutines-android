package com.kantek.coroutines.views.adapters

import android.support.core.base.RecyclerAdapter
import android.support.core.base.RecyclerHolder
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.kantek.coroutines.R
import com.kantek.coroutines.models.Album
import kotlinx.android.synthetic.main.item_view_post.view.*

class AlbumAdapter(view: RecyclerView) : RecyclerAdapter<Album>(view) {
    var onItemClickListener: ((Album) -> Unit)? = null

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int) = object :
        RecyclerHolder<Album>(p0, R.layout.item_view_album) {
        override fun bind(item: Album) {
            super.bind(item)
            itemView.apply {
                txtTitle.text = item.title
                setOnClickListener { onItemClickListener?.invoke(item) }
            }
        }
    }

}

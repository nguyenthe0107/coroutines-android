package com.kantek.coroutines.views.adapters

import android.support.core.base.RecyclerAdapter
import android.support.core.base.RecyclerHolder
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.kantek.coroutines.R
import com.kantek.coroutines.models.Photo
import kotlinx.android.synthetic.main.item_view_photo.view.*

class PhotoAdapter(view: androidx.recyclerview.widget.RecyclerView) : RecyclerAdapter<Photo>(view) {
    var onItemClickListener: ((Photo) -> Unit)? = null

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int) = object :
        RecyclerHolder<Photo>(p0, R.layout.item_view_photo) {
        override fun bind(item: Photo) {
            super.bind(item)
            itemView.apply {
                imgPhoto.setImageUrl(item.thumbnailUrl)
                txtTitle.text = item.title
                setOnClickListener { onItemClickListener?.invoke(item) }
            }
        }
    }
}

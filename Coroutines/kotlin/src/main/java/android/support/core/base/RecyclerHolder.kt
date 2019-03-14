package android.support.core.base

import android.arch.lifecycle.Observer
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

@Suppress("UNCHECKED_CAST")
open class RecyclerHolder<T>(private val parent: ViewGroup, @LayoutRes id: Int) : RecyclerView.ViewHolder(inflate(parent, id)), Observer<T> {

    var item: T? = null
        private set

    private val mAdapter get() = ((parent as RecyclerView).adapter as RecyclerAdapter<*>)

    open fun bind(item: T) {
        this.item = item
    }

    open fun bind(item: T, payload: Any?) {
        this.item = item
    }

    open fun onRecycled() {
    }

    override fun onChanged(t: T?) {
        bind(t!!)
    }

    fun fitSpanCount(count: Int, byWidth: Boolean) {
        itemView.layoutParams.apply {
            width = parent.measuredWidth / count
            height = width
        }
    }

    val isAtFirst get() = adapterPosition == 0

    open val isAtLast: Boolean
        get() {
            return adapterPosition == mAdapter.itemCount - 1
        }

    companion object {
        fun inflate(parent: ViewGroup, id: Int): View =
            LayoutInflater.from(parent.context)
                .inflate(id, parent, false)
    }

}
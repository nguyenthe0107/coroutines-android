package com.kantek.coroutines.widgets

import android.content.Context
import android.support.core.extensions.dpToPx
import android.support.v4.widget.CircularProgressDrawable
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import com.bumptech.glide.Glide

open class LightWeightImageView : AppCompatImageView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defaultAttr: Int) : super(context, attributeSet, defaultAttr)

    fun setImageUrl(url: String) {
        Glide.with(this)
            .load(url)
            .placeholder(CircularProgressDrawable(context).apply {
                centerRadius = context.dpToPx(6f)
                strokeWidth = context.dpToPx(2f)
                start()
            })
            .into(this)
    }
}
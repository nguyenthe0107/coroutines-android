package com.kantek.coroutines.extensions

import android.os.Handler
import android.view.View
import android.widget.TextView
import com.kantek.coroutines.R
import kotlinx.android.synthetic.main.activity_main.view.*


fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun TextView.setupStatusChanged(it: Boolean, delay: Handler) {
    if (it) {
        setText(R.string.warning_online)
        setBackgroundResource(R.color.colorPrimary)
        delay.postDelayed({ txtNetworkStatus.hide() }, 1000)
    } else {
        show()
        setText(R.string.warning_offline)
        setBackgroundResource(R.color.black)
    }
}
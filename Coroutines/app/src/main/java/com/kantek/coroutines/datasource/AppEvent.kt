package com.kantek.coroutines.datasource

import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.support.core.di.Inject
import android.support.core.event.BroadcastEvent
import android.support.core.utils.DriverUtils

@Inject(true)
class AppEvent(context: Context) {
    val networkChanged = NetworkEvent(context)
}

class NetworkEvent(context: Context) : BroadcastEvent<Boolean>(context) {
    init {
        filter = "android.net.conn.CONNECTIVITY_CHANGE"
        convertToResult = { DriverUtils.isNetworkEnabled(context) }
    }

    override fun observe(owner: LifecycleOwner, function: (Boolean?) -> Unit) {
        super.observe(owner) {
            if (it!!) function(true)
        }
    }

    fun listen(owner: LifecycleOwner, function: (Boolean?) -> Unit) {
        super.observe(owner, function)
    }

}
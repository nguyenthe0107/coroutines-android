package android.support.core.functional

import android.support.core.base.BaseActivity
import android.support.core.base.BaseFragment
import android.support.core.lifecycle.ResultLifecycle

interface Dispatcher {
    fun getResultLifecycle(): ResultLifecycle {
        return when (this) {
            is BaseActivity -> resultLife
            is BaseFragment -> resultLife
            else -> throw UnsupportedOperationException("Not support for ${this.javaClass.name}")
        }
    }
}
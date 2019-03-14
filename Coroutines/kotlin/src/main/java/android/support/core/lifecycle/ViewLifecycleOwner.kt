package android.support.core.lifecycle

import android.arch.lifecycle.LifecycleOwner

class ViewLifecycleOwner : LifecycleOwner {
    private val mRegistry = LifeRegistry(this)

    override fun getLifecycle(): LifeRegistry {
        return mRegistry
    }
}
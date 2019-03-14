package android.support.core.lifecycle

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry

class LifeRegistry(provider: LifecycleOwner) : LifecycleRegistry(provider) {

    var isCreated = false
        private set

    private var isUsedToDestroyed = false

    var isReCreated: Boolean = false
        private set

    fun create(): LifeRegistry {
        synchronized(this) {
            handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            isCreated = true
            if (isUsedToDestroyed) isReCreated = true
        }
        return this
    }

    fun start(): LifeRegistry {
        synchronized(this) {
            if (!isCreated) create()
            handleLifecycleEvent(Lifecycle.Event.ON_START)
        }
        return this
    }

    fun resume(): LifeRegistry {
        synchronized(this) {
            if (!isCreated) create().start()
            handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }
        return this
    }

    fun pause(): LifeRegistry {
        synchronized(this) {
            if (currentState == Lifecycle.State.RESUMED)
                handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        }
        return this
    }

    fun stop(): LifeRegistry {
        synchronized(this) {
            isReCreated = false
            when (currentState) {
                Lifecycle.State.STARTED ->
                    handleLifecycleEvent(Lifecycle.Event.ON_STOP)
                Lifecycle.State.RESUMED -> {
                    handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
                    handleLifecycleEvent(Lifecycle.Event.ON_STOP)
                }
                else -> {
                }
            }
        }
        return this
    }

    fun destroy(): LifeRegistry {
        synchronized(this) {
            if (!isCreated) return this
            when (currentState) {
                Lifecycle.State.CREATED -> {
                    handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                }
                Lifecycle.State.STARTED -> {
                    handleLifecycleEvent(Lifecycle.Event.ON_STOP)
                    handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                }
                Lifecycle.State.RESUMED -> {
                    handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
                    handleLifecycleEvent(Lifecycle.Event.ON_STOP)
                    handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                }
                else -> {
                }
            }
            isCreated = false
            isUsedToDestroyed = true
        }
        return this
    }
}
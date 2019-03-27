package android.support.core.event

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent

abstract class ForwardEvent<T : Any, K : Any> {
    open fun observe(owner: LifecycleOwner, function: (T?) -> Unit) {
        ForwardEvent.Notify(owner, function).apply {
            val event = registry(this)
            onDestroy = { unRegistry(event) }
        }
    }

    protected abstract fun registry(notify: ForwardEvent.Notify<T?>): K

    protected abstract fun unRegistry(event: K)

    class Notify<T>(private val owner: LifecycleOwner, private val function: (T?) -> Unit) {
        private var isCalled: Boolean = false
        private var mValue: T? = null
        internal var onDestroy: (() -> Unit)? = null

        init {
            owner.lifecycle.addObserver(object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                fun onDestroy() {
                    owner.lifecycle.removeObserver(this)
                    onDestroy?.invoke()
                }

                @OnLifecycleEvent(Lifecycle.Event.ON_START)
                fun onStart() {
                    if (isCalled) {
                        function(mValue)
                        synchronized(isCalled) {
                            isCalled = false
                            mValue = null
                        }
                    }
                }
            })
        }

        fun call(value: T?) {
            if (owner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                function(value)
            } else {
                synchronized(isCalled) {
                    isCalled = true
                    mValue = value
                }
            }
        }
    }
}



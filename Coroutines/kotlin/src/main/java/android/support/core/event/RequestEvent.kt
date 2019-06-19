package android.support.core.event

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData

open class RefreshEvent<T>(
    private val owner: LifecycleOwner,
    /**
     * In milliseconds
     */
    private val timeRate: Long = 200
) : MediatorLiveData<T>() {
    private var mOnActivated: (() -> Unit)? = null
    private var mActivated = false
    private var mLastCalled = 0L

    open fun addEvent(event: ForwardEvent<out Any, out Any>, function: ((Any?) -> Unit)? = null) {
        event.observe(owner) {
            if (function != null) function(it) else onEvent(it)
        }
    }

    open fun addEvent(event: LiveData<out Any>, function: ((Any?) -> Unit)? = null) {
        addSource(event) {
            if (function != null) function(it) else onEvent(it)
        }
    }

    /**
     * Listen events (Network, Location,...) and call refresh if needed
     * @param data list of LiveData - Check if data is not set value or not loaded yet
     * then call refresh to notify observers
     */
    fun addEvent(event: ForwardEvent<out Any, out Any>, vararg data: LiveData<out Any?>, function: ((Any?) -> Unit)? = null) {
        event.observe(owner) {
            val shouldRefresh = data.fold(false) { acc, item -> acc || item.value == null }
            if (!shouldRefresh) return@observe
            if (function != null) function(it) else onEvent(it)
        }
    }

    protected open fun onEvent(eventValue: Any?) {
        call()
    }

    fun onActivated(function: RefreshEvent<T>.() -> Unit): MediatorLiveData<T> {
        return MediatorLiveData<T>().also { next ->
            next.addSource(this) {
                activate(function)
                next.value = it
            }
        }
    }

    private fun activate(function: RefreshEvent<T>.() -> Unit) {
        synchronized(this) {
            if (!mActivated) {
                mActivated = true
                function(this)
            }
        }
    }

    fun callIfNotActivated() {
        if (!mActivated) call()
    }

    fun call() {
        value = value
    }

    override fun setValue(value: T?) {
        if (shouldCall()) super.setValue(value)
    }

    private fun shouldCall(): Boolean {
        if (timeRate <= 0) return false
        val current = System.currentTimeMillis()
        if (current - mLastCalled < timeRate) return false
        mLastCalled = current
        return true
    }
}

class RequestEvent<T>(owner: LifecycleOwner) : RefreshEvent<T>(owner) {
    private var mStoreValue: T? = null

    override fun onEvent(eventValue: Any?) {
        if (mStoreValue != null) postValue(mStoreValue)
    }

    override fun setValue(value: T?) {
        synchronized(this) { mStoreValue = value }
        super.setValue(value)
    }

    /**
     * Put value into temporary and waiting for event call to forward
     * @param value Value in refreshing
     */
    infix fun put(value: T) {
        mStoreValue = value
    }
}
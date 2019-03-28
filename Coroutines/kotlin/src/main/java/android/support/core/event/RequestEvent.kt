package android.support.core.event

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.support.core.extensions.call

open class RefreshEvent<T>(private val owner: LifecycleOwner) : MutableLiveData<T>() {
    open fun addEvent(event: ForwardEvent<out Any, out Any>) {
        event.observe(owner, this::onEvent)
    }

    /**
     * Listen events (Network, Location,...) and call refresh if needed
     * @param data list of LiveData - Check if data is not set value or not loaded yet
     * then call refresh to notify observers
     */
    fun addEvent(event: ForwardEvent<out Any, out Any>, vararg data: LiveData<out Any>) {
        event.observe(owner) {
            val shouldRefresh = data.fold(false) { acc, item -> acc || item.value == null }
            if (shouldRefresh) onEvent(it)
        }
    }

    protected open fun onEvent(eventValue: Any?) {
        call()
    }

}

class RequestEvent<T>(owner: LifecycleOwner) : RefreshEvent<T>(owner) {
    private var mStoreValue: T? = null

    override fun onEvent(eventValue: Any?) {
        postValue(mStoreValue)
    }

    override fun setValue(value: T?) {
        synchronized(this) { mStoreValue = value }
        super.setValue(value)
    }

    infix fun put(value: T) {
        mStoreValue = value
    }
}
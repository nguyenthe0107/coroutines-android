package android.support.core.extensions

import android.arch.lifecycle.*
import android.support.core.base.BaseFragment
import android.support.core.base.BaseViewModel
import android.support.core.event.SingleLiveEvent
import android.support.core.helpers.AppExecutors
import android.support.v4.app.Fragment
import kotlinx.coroutines.CoroutineScope

fun <T> LiveData<T>.observe(owner: LifecycleOwner, function: (T?) -> Unit) {
    if (owner is Fragment) {
        observe(if (owner is BaseFragment) owner.viewLife else owner.viewLifecycleOwner, Observer(function))
    } else {
        observe(owner, Observer(function))
    }
}

fun <T> MutableLiveData<T>.call() {
    value = value
}

fun <T> MutableLiveData<T>.refresh() {
    if (value != null) value = value
}

fun <T> MutableLiveData<T>.loadOnDisk(function: () -> T?): LiveData<T> {
    AppExecutors.onDisk { postValue(function()) }
    return this
}

fun <T, V> LiveData<T>.map(function: (T?) -> V?): LiveData<V> {
    return MediatorLiveData<V>().also { next ->
        next.addSource(this) {
            next.value = function(it)
        }
    }
}

fun <T, V> LiveData<T>.switchMap(function: (T?) -> LiveData<V>) = Transformations.switchMap(this) {
    function(it)
}

fun <T, V> LiveData<T>.mapLive(function: MutableLiveData<V>.(T?) -> Unit): LiveData<V> {
    val next = MediatorLiveData<V>()
    next.addSource(this) {
        function(next, it)
    }
    return next
}

fun <T, V> LiveData<T>.map(
    viewModel: BaseViewModel,
    loading: MutableLiveData<Boolean>? = viewModel.loading,
    error: SingleLiveEvent<out Throwable>? = viewModel.error,
    function: suspend CoroutineScope.(T?) -> V?
): LiveData<V> {
    return MediatorLiveData<V>().also { next ->
        next.addSource(this) {
            viewModel.launch(loading, error) {
                next.postValue(function(this, it))
            }
        }
    }
}

fun <T> LiveData<T>.submit(owner: LifecycleOwner) {
    observe(owner, Observer { })
}

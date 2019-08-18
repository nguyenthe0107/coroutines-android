package android.support.core.base

import android.support.core.event.LoadingEvent
import android.support.core.event.RefreshEvent
import android.support.core.event.SingleLiveEvent
import android.support.core.extensions.withIO
import android.support.core.lifecycle.LifeRegistry
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : ViewModel(), LifecycleOwner {
    val loading = LoadingEvent()
    val error = SingleLiveEvent<Throwable>()
    val refresh = RefreshEvent<Any>(this)

    private val mLife = LifeRegistry(this)
    private val mScope = ViewModelScope()

    override fun getLifecycle() = mLife

    init {
        mLife.create().start()
    }

    fun launch(
        loading: MutableLiveData<Boolean>? = this@BaseViewModel.loading,
        error: SingleLiveEvent<out Throwable>? = this@BaseViewModel.error,
        block: suspend CoroutineScope.() -> Unit
    ) = mScope.launch {
        try {
            loading?.postValue(true)
            withIO(block)
        } catch (e: CancellationException) {
            Log.i(this@BaseViewModel.javaClass.name, e.message ?: "Unknown")
        } catch (e: Throwable) {
            @Suppress("UNCHECKED_CAST")
            (error as? MutableLiveData<Throwable>)?.postValue(e)
            e.printStackTrace()
        } finally {
            loading?.postValue(false)
        }
    }

    fun <T, V> LiveData<T>.next(
        loading: MutableLiveData<Boolean>? = this@BaseViewModel.loading,
        error: SingleLiveEvent<out Throwable>? = this@BaseViewModel.error,
        function: suspend CoroutineScope.(T?) -> V?
    ): LiveData<V> = MediatorLiveData<V>().also { next ->
        next.addSource(this) {
            launch(loading, error) {
                next.postValue(function(this, it))
            }
        }
    }

    fun <T, V> LiveData<T>.switchTo(
        loading: MutableLiveData<Boolean>? = this@BaseViewModel.loading,
        error: SingleLiveEvent<out Throwable>? = this@BaseViewModel.error,
        function: suspend CoroutineScope.(T?) -> LiveData<V>
    ): LiveData<V> = MediatorLiveData<V>().also { result ->
        result.addSource<T>(this, object : Observer<T> {
            var mSource: LiveData<V>? = null

            override fun onChanged(x: T?) {
                launch(loading, error) {
                    val newLiveData = function(this@launch, x)
                    if (mSource == newLiveData) return@launch
                    mSource?.apply { result.removeSource(this) }
                    mSource = newLiveData
                    mSource?.apply { result.addSource(this) { y -> result.value = y } }
                }
            }
        })
    }

    suspend fun <T> onMain(block: suspend CoroutineScope.() -> T) =
        withContext(Dispatchers.Main, block)

    suspend fun <T> onBackground(block: suspend CoroutineScope.() -> T) =
        withContext(Dispatchers.IO, block)

    override fun onCleared() {
        mLife.stop().destroy()
        mScope.coroutineContext.cancel()
    }

    private class ViewModelScope : CoroutineScope {
        override val coroutineContext: CoroutineContext = Job() + Dispatchers.Main
    }
}
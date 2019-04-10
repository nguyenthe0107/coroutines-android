package android.support.core.base

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.support.core.event.LoadingEvent
import android.support.core.event.RefreshEvent
import android.support.core.event.SingleLiveEvent
import android.support.core.lifecycle.LifeRegistry
import android.util.Log
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
    ) {
        mScope.launch {
            try {
                loading?.postValue(true)
                block()
            } catch (e: CancellationException) {
                Log.i(this@BaseViewModel.javaClass.name, e.message ?: "Unknown")
            } catch (e: Throwable) {
                e.printStackTrace()
                Log.e("CALL_ERROR", "${e.javaClass.name} ${e.message ?: "Unknown"}")
                @Suppress("UNCHECKED_CAST")
                (error as? MutableLiveData<Throwable>)?.postValue(e)
            } finally {
                loading?.postValue(false)
            }
        }
    }

    override fun onCleared() {
        System.gc()
        mLife.stop().destroy()
        mScope.coroutineContext.cancel()
    }

    private class ViewModelScope : CoroutineScope {
        override val coroutineContext: CoroutineContext = Job() + Dispatchers.IO
    }
}
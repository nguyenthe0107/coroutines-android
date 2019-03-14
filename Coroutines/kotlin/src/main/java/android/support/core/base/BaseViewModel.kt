package android.support.core.base

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.support.core.event.LoadingEvent
import android.support.core.event.SingleLiveEvent
import android.support.core.lifecycle.LifeRegistry
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : ViewModel(), LifecycleOwner {
    val loading = LoadingEvent()
    val error = SingleLiveEvent<Throwable>()
    val refresh = SingleLiveEvent<Any>()

    private val mLife = LifeRegistry(this)
    private var mCoroutineScope: MainScope? = null
    private val mScope: MainScope
        get() {
            if (mCoroutineScope == null) mCoroutineScope = MainScope()
            return mCoroutineScope!!
        }

    override fun getLifecycle() = mLife

    init {
        mLife.create().start()
    }

    fun launch(
        loading: MutableLiveData<Boolean>? = this@BaseViewModel.loading,
        error: SingleLiveEvent<Throwable>? = this@BaseViewModel.error,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return mScope.launch {
            try {
                loading?.value = true
                block()
            } catch (e: Throwable) {
                if (error != null) error.value = e
            } finally {
                loading?.value = false
            }
        }
    }

    override fun onCleared() {
        mLife.stop().destroy()
        mCoroutineScope?.coroutineContext?.cancel()
    }

    private class MainScope : CoroutineScope {
        override val coroutineContext: CoroutineContext = Job() + Dispatchers.Main
    }
}
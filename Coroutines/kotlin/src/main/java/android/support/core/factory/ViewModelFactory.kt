package android.support.core.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.support.core.di.DependenceContext

@Suppress("UNCHECKED_CAST")
class ViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DependenceContext.sInstance.lookup(modelClass)!!.instance as T
    }

    companion object {
        var sInstance = ViewModelFactory()
    }
}

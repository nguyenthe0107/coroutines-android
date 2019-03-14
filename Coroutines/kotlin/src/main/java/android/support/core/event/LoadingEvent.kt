package android.support.core.event

import android.arch.lifecycle.MutableLiveData

open class LoadingEvent : MutableLiveData<Boolean>() {
    private var mNumOfLoading = 0

    override fun setValue(value: Boolean?) {
        synchronized(this) {
            if (value!!) {
                mNumOfLoading++
                if (shouldPost(true)) super.setValue(true)
            } else {
                mNumOfLoading--
                if (mNumOfLoading < 0) mNumOfLoading = 0
                if (mNumOfLoading == 0) super.setValue(false)
            }
        }
    }

    protected open fun shouldPost(b: Boolean) = this.value != b
}
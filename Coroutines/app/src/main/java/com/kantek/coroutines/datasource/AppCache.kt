package com.kantek.coroutines.datasource

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.support.core.di.Inject
import android.support.core.helpers.AppExecutors
import com.google.gson.Gson
import com.kantek.coroutines.models.User

@Inject(singleton = true)
class AppCache(context: Context) {
    private val mShared = context.getSharedPreferences("test:cache", Context.MODE_PRIVATE)
    private var mUser: User? = null
    var user: User?
        get() {
            if (mUser == null) {
                mUser = Gson().fromJson(mShared.getString(User::class.java.name, ""), User::class.java)
            }
            return mUser
        }
        set(value) {
            mUser = value
            mShared.edit().putString(User::class.java.name, Gson().toJson(user)).apply()
            userLive.postValue(value)
        }

    val userLive = MutableLiveData<User>().apply {
        AppExecutors.onDisk {
            postValue(user)
        }
    }
}

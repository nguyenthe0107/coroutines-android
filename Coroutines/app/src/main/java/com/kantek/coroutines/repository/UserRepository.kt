package com.kantek.coroutines.repository

import android.support.core.di.Repository
import com.kantek.coroutines.datasource.ApiService
import com.kantek.coroutines.datasource.AppCache
import com.kantek.coroutines.extensions.call

class UserRepository(
    private val apiService: ApiService,
    private val appCache: AppCache
) : Repository {
    fun update(vararg body: Pair<String, String>) =
        apiService.updateProfile(appCache.user!!.id, body.toMap()).call {
            appCache.user = this
        }
}

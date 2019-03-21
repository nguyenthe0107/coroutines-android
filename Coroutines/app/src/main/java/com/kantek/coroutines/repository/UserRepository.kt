package com.kantek.coroutines.repository

import android.support.core.di.Repository
import android.support.core.extensions.withIO
import com.kantek.coroutines.datasource.ApiService
import com.kantek.coroutines.datasource.AppCache
import com.kantek.coroutines.extensions.call

class UserRepository(
    private val apiService: ApiService,
    private val appCache: AppCache
) : Repository {
    suspend fun update(vararg body: Pair<String, String>) = withIO {
        apiService.updateProfile(appCache.user!!.id, body.toMap()).call {
            appCache.user = this
        }
    }
}

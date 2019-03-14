package com.kantek.coroutines.repository

import android.support.core.di.Repository
import com.kantek.coroutines.datasource.ApiService
import com.kantek.coroutines.datasource.AppCache
import com.kantek.coroutines.datasource.call
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(
    private val apiService: ApiService,
    private val appCache: AppCache
) : Repository {
    suspend fun update(vararg body: Pair<String, String>) = withContext(Dispatchers.IO) {
        apiService.updateProfile(appCache.user!!.id, body.toMap()).call().also {
            appCache.user = it
        }
    }
}

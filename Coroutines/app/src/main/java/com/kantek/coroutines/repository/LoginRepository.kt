package com.kantek.coroutines.repository

import android.support.core.di.Repository
import com.kantek.coroutines.datasource.ApiService
import com.kantek.coroutines.datasource.AppCache
import com.kantek.coroutines.datasource.call
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginRepository(
    private val apiService: ApiService,
    private val appCache: AppCache
) : Repository {

    suspend fun login(it: Pair<String?, String?>) = withContext(Dispatchers.IO) {
        val user = apiService.login().call()
        if (!user.email.equals(it.first, true)
            || !user.userName.equals(it.second, true)
        ) throw Throwable("Login fail")
        user.apply { appCache.user = this }
    }
}

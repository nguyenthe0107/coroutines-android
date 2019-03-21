package com.kantek.coroutines.repository

import android.support.core.di.Repository
import android.support.core.extensions.withIO
import com.kantek.coroutines.datasource.ApiService
import com.kantek.coroutines.datasource.AppCache
import com.kantek.coroutines.extensions.call

class LoginRepository(
    private val apiService: ApiService,
    private val appCache: AppCache
) : Repository {

    suspend fun login(it: Pair<String?, String?>) = withIO {
        apiService.login().call {
            if (!email.equals(it.first, true) || !userName.equals(it.second, true))
                throw Throwable("Login fail")
            appCache.user = this
        }
    }
}

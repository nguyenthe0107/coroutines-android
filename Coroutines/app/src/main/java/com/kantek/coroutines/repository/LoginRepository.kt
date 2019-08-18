package com.kantek.coroutines.repository

import android.support.core.di.Repository
import com.kantek.coroutines.app.fail
import com.kantek.coroutines.datasource.ApiService
import com.kantek.coroutines.datasource.AppCache
import com.kantek.coroutines.extensions.submit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class LoginRepository(
    private val apiService: ApiService,
    private val appCache: AppCache
) : Repository {

    suspend fun login(it: Pair<String?, String?>) = coroutineScope {
        apiService.login().submit().apply {
            if (!email.equals(it.first, true) || !userName.equals(it.second, true))
                fail("Login fail")
            appCache.user = this
        }
    }
}

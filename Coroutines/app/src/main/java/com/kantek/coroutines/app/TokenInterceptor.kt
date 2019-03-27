package com.kantek.coroutines.app

import android.support.core.di.Inject
import com.kantek.coroutines.datasource.AppCache
import okhttp3.Interceptor
import okhttp3.Response

@Inject(true)
class TokenInterceptor(private val appCache: AppCache) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (appCache.user != null) {
            val url = request.url().newBuilder().addQueryParameter("userId", appCache.user!!.id).build()
            request = request.newBuilder().url(url).build()
        }
        return chain.proceed(request)
    }
}

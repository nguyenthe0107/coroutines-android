package com.kantek.coroutines.app

import android.support.core.di.Provide
import android.support.core.factory.TLSSocketFactory
import com.google.gson.GsonBuilder
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import com.kantek.coroutines.BuildConfig
import com.kantek.coroutines.datasource.ApiService
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Suppress("unused")
class AppModule {
    @Provide
    fun provideInterceptorBuilder(): LoggingInterceptor.Builder = LoggingInterceptor.Builder()
        .loggable(BuildConfig.DEBUG)
        .setLevel(Level.BASIC)
        .log(Platform.INFO)
        .request("Request")
        .response("Response")

    @Provide
    fun provideOkHttpClientBuilder(): OkHttpClient.Builder {
        val tslFactory = TLSSocketFactory()
        return OkHttpClient.Builder().sslSocketFactory(tslFactory, tslFactory.systemDefaultTrustManager())
    }

    @Provide
    fun provideRetrofitBuilder(gsonConverterFactory: GsonConverterFactory) = Retrofit.Builder()
        .addConverterFactory(gsonConverterFactory)!!

    @Provide
    fun provideInterceptor(builder: LoggingInterceptor.Builder): LoggingInterceptor = builder
        .addHeader("Content-Type", "application/json")
        .build()

    @Provide
    fun provideOkHttpClient(
        client: OkHttpClient.Builder,
        loggingInterceptor: LoggingInterceptor
    ): OkHttpClient {
        return client.addInterceptor(loggingInterceptor)
            .build()
    }

    @Provide
    fun provideGsonConvertFactory() = GsonConverterFactory
        .create(
            GsonBuilder()
                .setDateFormat("yyyy-MM-dd hh:mm:ss")
                .create()
        )!!

    @Provide
    fun provideApiService(
        retrofitBuilder: Retrofit.Builder,
        client: OkHttpClient
    ) = retrofitBuilder
        .baseUrl("http://jsonplaceholder.typicode.com/")
        .client(client)
        .build()
        .create(ApiService::class.java)!!

}

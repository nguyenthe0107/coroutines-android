@file:Suppress("unused")

package com.kantek.coroutines.app

import android.content.Context
import android.support.core.di.Provide
import android.support.core.factory.TLSSocketFactory
import androidx.room.Room
import com.google.gson.GsonBuilder
import com.kantek.coroutines.BuildConfig
import com.kantek.coroutines.app.interceptor.Logger
import com.kantek.coroutines.app.interceptor.LoggingInterceptor
import com.kantek.coroutines.app.interceptor.TokenInterceptor
import com.kantek.coroutines.datasource.ApiService
import com.kantek.coroutines.datasource.AppDatabase
import com.kantek.coroutines.datasource.SoapService
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

class AppModule {

    @Provide
    fun provideLoggingInterceptor(): LoggingInterceptor = LoggingInterceptor.Builder()
        .loggable(BuildConfig.DEBUG)
        .setLevel(Logger.Level.BASIC)
        .log(Platform.INFO)
        .request("Request")
        .response("Response")
        .addHeader("Content-Type", "application/json")
        .build()

    @Provide
    fun provideOkHttpClient(
        loggingInterceptor: LoggingInterceptor,
        token: TokenInterceptor
    ): OkHttpClient {
        val tslFactory = TLSSocketFactory()
        return OkHttpClient.Builder()
            .sslSocketFactory(tslFactory, tslFactory.systemDefaultTrustManager())
            .addInterceptor(loggingInterceptor)
            .addInterceptor(token)
            .build()
    }

    @Provide
    fun provideGsonConvertFactory() = GsonConverterFactory
        .create(GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create())!!


    @Provide
    fun provideScalarConvertFactory() = SimpleXmlConverterFactory
        .create()!!

    @Provide(false)
    fun provideRetrofitBuilder(
        client: OkHttpClient
    ) = Retrofit.Builder()
        .client(client)!!

    @Provide
    fun provideApiService(
        retrofitBuilder: Retrofit.Builder,
        gsonConverterFactory: GsonConverterFactory
    ) = retrofitBuilder
        .addConverterFactory(gsonConverterFactory)
        .baseUrl("http://jsonplaceholder.typicode.com/")
        .build()
        .create(ApiService::class.java)!!

    @Provide
    fun provideSoapService(
        retrofitBuilder: Retrofit.Builder,
        factory: SimpleXmlConverterFactory
    ) = retrofitBuilder
        .addConverterFactory(factory)
        .baseUrl("https://www.image3d.com/retroviewer/index.php/api/")
        .build()
        .create(SoapService::class.java)!!

    @Provide
    fun provideAppDatabase(context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
            .build()
}

package com.kantek.coroutines.datasource

import com.kantek.coroutines.models.LoginRequestEnvelope
import com.kantek.coroutines.models.LoginResponseEnvelope
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface SoapService {
    @Headers(
        "Content-Type: text/xml",
        "Accept-Charset: utf-8"
    )
    @POST("index/index/")
    fun login(@Body build: LoginRequestEnvelope): Call<LoginResponseEnvelope>
}

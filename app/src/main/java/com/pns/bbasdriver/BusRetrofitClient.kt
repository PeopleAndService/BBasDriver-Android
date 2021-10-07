package com.pns.bbasdriver

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object BusRetrofitClient {
    private val gson = GsonBuilder().setLenient().create()
    private const val BASE_URL = "http://openapi.tago.go.kr/openapi/service/"

    private val instance = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    val mInstance = instance.create(BusRetrofitService::class.java)
}
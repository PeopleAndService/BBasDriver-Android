package com.pns.bbasdriver

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface RetrofitService {
    @POST("driverSign")
    fun sign(
        @Body userRequestBody: UserRequestBody
    ): Call<BaseResponseModel<User>>

    @PUT("driver")
    fun attendance(
        @Body userRequestBody: UserRequestBody
    ) : Call<BaseResponseModel<User>>
}
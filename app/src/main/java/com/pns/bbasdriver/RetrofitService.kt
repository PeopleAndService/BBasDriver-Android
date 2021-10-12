package com.pns.bbasdriver

import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT

interface RetrofitService {
    @POST("driverSign")
    fun sign(
        @Body userRequestBody: UserRequestBody
    ): Call<UserBaseResponseModel<User>>

    @PUT("driver")
    fun attendance(
        @Body attendanceRequestBody: AttendanceRequestBody
    ): Call<UserBaseResponseModel<User>>

    @HTTP(method = "DELETE", path = "driver", hasBody = true)
    fun withdraw(
        @Body userIdRequestBody: UserIdRequestBody
    ): Call<UserBaseResponseModel<JSONObject>>

    @POST("queueInfo")
    fun getInfo(
        @Body busRequestBody: BusRequestBody
    ): Call<BusBaseResponseModel>
}
package com.pns.bbasdriver

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BusRetrofitService {
    @GET("BusRouteInfoInqireService/getRouteAcctoThrghSttnList")
    fun getRoute(
        @Query("ServiceKey") serviceKey: String,
        @Query("cityCode") cityCode: String,
        @Query("routeId") routeId: String,
        @Query("numOfRows") numOfRows: Int,
        @Query("_type") _type: String
    ): Call<BusBaseResponseModel<List<BusRoute>>>

    @GET("BusLcInfoInqireService/getRouteAcctoBusLcList")
    fun getLocation(
        @Query("ServiceKey") serviceKey: String,
        @Query("cityCode") cityCode: String,
        @Query("routeId") routeId: String,
        @Query("numOfRows") numOfRows: Int,
        @Query("_type") _type: String
    ): Call<BusBaseResponseModel<List<BusLocation>>>
}
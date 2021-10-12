package com.pns.bbasdriver

import com.google.gson.annotations.SerializedName

data class BusRequestBody(

    @SerializedName("cityCode")
    val cityCode: String,

    @SerializedName("busRouteId")
    val busRouteId: String,

    @SerializedName("vehicleId")
    val vehicleId: String

)
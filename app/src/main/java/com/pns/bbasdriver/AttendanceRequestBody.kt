package com.pns.bbasdriver

import com.google.gson.annotations.SerializedName

data class AttendanceRequestBody(
    @SerializedName("did")
    val did: String,

    @SerializedName("vehicleId")
    val vehicleId: String,

    @SerializedName("busRouteId")
    val busRouteId: String

)
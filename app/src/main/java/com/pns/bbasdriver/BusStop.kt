package com.pns.bbasdriver

import com.google.gson.annotations.SerializedName

data class BusStop(
    @SerializedName("stationName")
    val stationName: String,

    @SerializedName("waiting")
    val waiting: Boolean
)
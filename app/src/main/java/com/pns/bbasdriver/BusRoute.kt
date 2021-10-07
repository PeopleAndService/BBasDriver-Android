package com.pns.bbasdriver

import com.google.gson.annotations.SerializedName

data class BusRoute(
    @SerializedName("gpslati")
    val gpslati: Double,

    @SerializedName("gpslong")
    val gpslong: Double,

    @SerializedName("nodeid")
    val nodeid: String,

    @SerializedName("nodenm")
    val nodeName: String,

    @SerializedName("nodeno")
    val nodeno: String,

    @SerializedName("nodeord")
    val nodeord: Int,

    @SerializedName("routeid")
    val routeid: String

)
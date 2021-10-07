package com.pns.bbasdriver

import com.google.gson.annotations.SerializedName

data class BusLocation(

    @SerializedName("nodeid")
    val nodeid: String,

    @SerializedName("nodenm")
    val nodeName: String,

    @SerializedName("nodeord")
    val nodeord: Int,

    @SerializedName("vehicleno")
    val vehicleno: String

)
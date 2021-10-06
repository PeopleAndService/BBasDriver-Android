package com.pns.bbasdriver

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("did")
    val userId: String,

    @SerializedName("name")
    val userName: String,

    @SerializedName("pushToken")
    val pushToken: String?,

    @SerializedName("pushSetting")
    val pushSetting: Boolean,

    @SerializedName("verified")
    val isVerified: Boolean,

    @SerializedName("vehicleId")
    val vehicleId: String?,

    @SerializedName("busRouteId")
    val busRouteId: String?

)
package com.pns.bbasdriver

import com.google.gson.annotations.SerializedName

data class BusBaseResponseModel(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("result")
    val result: List<BusStop>,

    @SerializedName("boardingStatus")
    val boardingStatus: Boolean,

    @SerializedName("message")
    val message: RecentResult
)

data class RecentResult(
    @SerializedName("recentResult")
    val recentResult: Notice
)

data class Notice(
    @SerializedName("stationName")
    val stationName: String,

    @SerializedName("queueTime")
    val queueTime: String
)
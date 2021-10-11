package com.pns.bbasdriver

import com.google.gson.annotations.SerializedName

data class UserBaseResponseModel<T>(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("result")
    val result: T
)

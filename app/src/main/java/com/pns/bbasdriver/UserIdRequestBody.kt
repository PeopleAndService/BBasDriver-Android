package com.pns.bbasdriver

import com.google.gson.annotations.SerializedName

data class UserIdRequestBody(
    @SerializedName("did")
    val did: String
)
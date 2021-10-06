package com.pns.bbasdriver

import com.google.gson.annotations.SerializedName

data class UserRequestBody(
    @SerializedName("did")
    val did: String,

    @SerializedName("name")
    val name: String

)
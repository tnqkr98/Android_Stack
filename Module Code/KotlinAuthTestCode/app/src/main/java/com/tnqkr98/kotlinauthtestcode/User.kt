package com.tnqkr98.kotlinauthtestcode

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("code") val code: Int,
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("message") val message: String
)
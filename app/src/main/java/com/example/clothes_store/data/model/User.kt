package com.example.clothes_store.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") val id : Int = 0,
    @SerializedName("email") val email : String = "",
    @SerializedName("password") val password : String = "",
    @SerializedName("role") val role : String = ""
)
